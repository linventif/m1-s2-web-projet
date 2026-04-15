/**
 * Récupère les données météo via l'API
 */
function fetchWeather() {
    const dateInput = document.getElementById("date");
    const addressInput = document.getElementById("address");
    const durationInput = document.getElementById("duration");

    const date = dateInput ? dateInput.value : "";
    const address = addressInput ? addressInput.value : "";
    const duration = durationInput && durationInput.value ? durationInput.value : "60";

    // On ne lance l'alerte que si l'appel est manuel (via bouton). 
    // Pour l'auto-refresh, on vérifie juste silencieusement.
    if (!date || !address) return;

    const url = `/api/weather/stats?date=${encodeURIComponent(date)}&address=${encodeURIComponent(address)}&duration=${duration}`;

    fetch(url)
        .then((response) => {
            if (!response.ok) throw new Error("Erreur météo");
            return response.json();
        })
        .then((data) => {
            const img = document.getElementById("weather-img");
            const hiddenInput = document.getElementById("weather-icon-hidden");

            if (data.weatherIndicator) {
                img.src = `/images/weather/${data.weatherIndicator}.png`;
                hiddenInput.value = data.weatherIndicator;
            } else {
                img.src = "/images/weather/unknown.png";
                hiddenInput.value = "";
            }

            document.getElementById("weather-temp").value = data.averageTemperature || "--";
            document.getElementById("weather-wind").value = data.averageWindSpeed || "--";
            document.getElementById("weather-precip").value = data.averagePrecipitation || "--";

            document.getElementById("weather-max").value = data.maxTemperature || "";
            document.getElementById("weather-min").value = data.minTemperature || "";
            document.getElementById("weather-apparent").value = data.averageApparentTemperature || "";
        })
        .catch((error) => {
            console.error("Erreur météo:", error);
        });
}

/**
 * Logique d'actualisation automatique avec Debounce
 */
let weatherTimeout;
function autoUpdateWeather() {
    // On annule le timer précédent si l'utilisateur continue de taper
    clearTimeout(weatherTimeout);

    // On attend 800ms de silence avant de lancer la requête
    weatherTimeout = setTimeout(() => {
        const date = document.getElementById("date").value;
        const address = document.getElementById("address").value;

        if (date && address && address.trim() !== "") {
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
    if (!dateInput || (dateInput.value && dateInput.value.trim() !== "")) {
        return;
    }
    dateInput.value = formatDateTimeLocal(new Date());
}

function inferCityFromTimezone() {
    try {
        const timeZone = Intl.DateTimeFormat().resolvedOptions().timeZone || "";
        const parts = timeZone.split("/");
        const maybeCity = parts[parts.length - 1] || "";
        const normalized = maybeCity.replace(/_/g, " ").trim();
        if (normalized) return normalized;
    } catch (_error) {}
    return "Position inconnue";
}

function setAddressValue(addressInput, value) {
    if (!addressInput || !value || !value.trim()) return;
    addressInput.value = value;
    addressInput.dataset.autofilled = "true";
    // On déclenche l'auto-update car la valeur a changé via script
    autoUpdateWeather();
}

function canAutoFillAddress(addressInput) {
    if (!addressInput) return false;
    const hasValue = addressInput.value.trim() !== "";
    const isAutofilled = addressInput.dataset.autofilled === "true";
    return !hasValue || isAutofilled;
}

function detectCityFromBrowserLocation() {
    const addressInput = document.getElementById("address");
    if (cityDetectionStarted || !addressInput || !canAutoFillAddress(addressInput)) return;
    cityDetectionStarted = true;

    const timezoneCity = inferCityFromTimezone();
    if (canAutoFillAddress(addressInput)) {
        setAddressValue(addressInput, timezoneCity);
    }

    if (!navigator.geolocation) return;

    navigator.geolocation.getCurrentPosition(
        async (position) => {
            if (!canAutoFillAddress(addressInput)) return;

            const latitude = position.coords.latitude;
            const longitude = position.coords.longitude;
            const coordsFallback = `${latitude.toFixed(5)}, ${longitude.toFixed(5)}`;
            setAddressValue(addressInput, coordsFallback);

            try {
                const response = await fetch(`/api/weather/reverse-city?lat=${encodeURIComponent(latitude)}&lon=${encodeURIComponent(longitude)}`);
                if (!response.ok) return;
                const data = await response.json();
                if (data && data.city && data.city.trim() !== "") {
                    if (canAutoFillAddress(addressInput)) {
                        setAddressValue(addressInput, data.city);
                    }
                }
            } catch (error) {
                console.warn("Géolocalisation ville impossible:", error);
            }
        },
        () => {},
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
        const currentValue = sportNameInput.value.trim().toLowerCase();
        const match = optionElements.find((option) => option.value.trim().toLowerCase() === currentValue);
        sportIdInput.value = match ? match.dataset.id || "" : "";
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
        autoUpdateWeather(); // Déclenche l'update au clavier
    });

    addressInput.addEventListener("focus", detectCityFromBrowserLocation, { once: true });
}

/**
 * Initialise les écouteurs pour l'actualisation automatique
 */
function initWeatherListeners() {
    const dateInput = document.getElementById("date");
    const durationInput = document.getElementById("duration");

    if (dateInput) {
        dateInput.addEventListener("change", autoUpdateWeather);
    }
    if (durationInput) {
        durationInput.addEventListener("input", autoUpdateWeather);
    }
}

function initWorkoutFormEnhancements() {
    setDefaultWorkoutDate();
    initSportSuggestions();
    initAddressInputTracking();
    initWeatherListeners();
    detectCityFromBrowserLocation();
    
    // Premier essai après avoir mis la date par défaut
    autoUpdateWeather();
}

// Lancement au chargement de la page
if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", initWorkoutFormEnhancements);
} else {
    initWorkoutFormEnhancements();
}