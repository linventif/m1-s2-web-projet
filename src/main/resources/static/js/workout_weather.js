/**
 * Met à jour la valeur d'un élément HTML de manière sécurisée
 */
const updateElementValue = (id, value) => {
    const el = document.getElementById(id);
    if (el) {
        el.value = value ?? "--";
    }
};

/**
 * Récupère les données météo via l'API
 */
function fetchWeather() {
    const date = document.getElementById("date")?.value ?? "";
    const address = document.getElementById("address")?.value ?? "";
    const duration = document.getElementById("duration")?.value || "60";

    if (!date || !address) return;

    const url = `/api/weather/stats?date=${encodeURIComponent(date)}&address=${encodeURIComponent(address)}&duration=${duration}`;

    fetch(url)
        .then((response) => {
            if (!response.ok) throw new Error(`Erreur météo: ${response.status}`);
            return response.json();
        })
        .then((data) => {
            const img = document.getElementById("weather-img");
            const hiddenInput = document.getElementById("weather-icon-hidden");

            if (data?.weatherIndicator) {
                if (img) img.src = `/images/weather/${data.weatherIndicator}.png`;
                if (hiddenInput) hiddenInput.value = data.weatherIndicator;
            } else {
                if (img) img.src = "/images/weather/unknown.png";
                if (hiddenInput) hiddenInput.value = "";
            }

            updateElementValue("weather-temp", data?.averageTemperature);
            updateElementValue("weather-wind", data?.averageWindSpeed);
            updateElementValue("weather-precip", data?.averagePrecipitation);
            updateElementValue("weather-max", data?.maxTemperature);
            updateElementValue("weather-min", data?.minTemperature);
            updateElementValue("weather-apparent", data?.averageApparentTemperature);
        })
        .catch((error) => {
            console.error("Échec de la récupération météo :", error.message);
        });
}

/**
 * Logique d'actualisation automatique avec Debounce
 */
let weatherTimeout;
function autoUpdateWeather() {
    clearTimeout(weatherTimeout);
    weatherTimeout = setTimeout(() => {
        const date = document.getElementById("date")?.value ?? "";
        const address = document.getElementById("address")?.value ?? "";

        if (date && address.trim() !== "") {
            fetchWeather();
        }
    }, 800);
}

let cityDetectionStarted = false;

function formatDateTimeLocal(date) {
    const pad = (value) => String(value).padStart(2, "0");
    return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
}

function setDefaultWorkoutDate() {
    const dateInput = document.getElementById("date");
    if (!dateInput || dateInput.value?.trim()) {
        return;
    }
    dateInput.value = formatDateTimeLocal(new Date());
}

function inferCityFromTimezone() {
    try {
        const timeZone = Intl.DateTimeFormat().resolvedOptions().timeZone ?? "";
        const parts = timeZone.split("/");
        const maybeCity = parts[parts.length - 1] ?? "";
        return maybeCity.replace(/_/g, " ").trim() || "Position inconnue";
    } catch (error) {
        console.warn("Impossible d'inférer la ville via Timezone:", error.message);
        return "Position inconnue";
    }
}

function setAddressValue(addressInput, value) {
    if (!addressInput || !value?.trim()) return;
    addressInput.value = value;
    addressInput.dataset.autofilled = "true";
    autoUpdateWeather();
}

function canAutoFillAddress(addressInput) {
    if (!addressInput) return false;
    const hasValue = addressInput.value?.trim() !== "";
    const isAutofilled = addressInput.dataset?.autofilled === "true";
    return !hasValue || isAutofilled;
}

function detectCityFromBrowserLocation() {
    const addressInput = document.getElementById("address");
    if (cityDetectionStarted || !canAutoFillAddress(addressInput)) return;
    cityDetectionStarted = true;

    setAddressValue(addressInput, inferCityFromTimezone());

    if (!navigator.geolocation) return;

    navigator.geolocation.getCurrentPosition(
        async (position) => {
            if (!canAutoFillAddress(addressInput)) return;

            const { latitude, longitude } = position.coords;
            const coordsFallback = `${latitude.toFixed(5)}, ${longitude.toFixed(5)}`;
            setAddressValue(addressInput, coordsFallback);

            try {
                const response = await fetch(`/api/weather/reverse-city?lat=${encodeURIComponent(latitude)}&lon=${encodeURIComponent(longitude)}`);
                if (!response.ok) return;
                const data = await response.json();
                if (data?.city?.trim()) {
                    setAddressValue(addressInput, data.city);
                }
            } catch (error) {
                console.warn("Géolocalisation ville impossible:", error.message);
            }
        },
        (error) => {
            console.warn("Accès géolocalisation refusé ou erreur:", error.message);
        },
        { enableHighAccuracy: false, timeout: 7000, maximumAge: 300000 }
    );
}

function initSportSuggestions() {
    const sportNameInput = document.getElementById("sport-name");
    const sportIdInput = document.getElementById("sport-id");
    const sportList = document.getElementById("sports-list");
    if (!sportNameInput || !sportIdInput || !sportList) return;

    const optionElements = Array.from(sportList.querySelectorAll("option"));
    const syncSportSelection = () => {
        const currentValue = sportNameInput.value?.trim().toLowerCase() ?? "";
        const match = optionElements.find((option) => option.value?.trim().toLowerCase() === currentValue);
        sportIdInput.value = match?.dataset?.id ?? "";
        sportNameInput.setCustomValidity(sportIdInput.value ? "" : "Choisissez un sport dans la liste proposée.");
    };

    sportNameInput.addEventListener("input", syncSportSelection);
    sportNameInput.addEventListener("change", syncSportSelection);
    sportNameInput.addEventListener("blur", syncSportSelection);
    syncSportSelection();
}

function initAddressInputTracking() {
    const addressInput = document.getElementById("address");
    if (!addressInput) return;

    addressInput.addEventListener("input", () => {
        addressInput.dataset.autofilled = "false";
        autoUpdateWeather();
    });

    addressInput.addEventListener("focus", detectCityFromBrowserLocation, { once: true });
}

function initWeatherListeners() {
    document.getElementById("date")?.addEventListener("change", autoUpdateWeather);
    document.getElementById("duration")?.addEventListener("input", autoUpdateWeather);
}

function initWorkoutFormEnhancements() {
    setDefaultWorkoutDate();
    initSportSuggestions();
    initAddressInputTracking();
    initWeatherListeners();
    detectCityFromBrowserLocation();
    autoUpdateWeather();
}

if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", initWorkoutFormEnhancements);
} else {
    initWorkoutFormEnhancements();
}