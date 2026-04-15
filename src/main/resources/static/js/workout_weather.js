function fetchWeather() {
    const dateInput = document.getElementById('date');
    const addressInput = document.getElementById('address');
    const durationInput = document.getElementById('duration');
    const date = dateInput ? dateInput.value : '';
    const address = addressInput ? addressInput.value : '';
    const duration = durationInput && durationInput.value ? durationInput.value : '60';

    if (!date || !address) {
        alert("Veuillez remplir la date et l'adresse pour obtenir la météo.");
        return;
    }

    // Afficher un état de chargement sur le bouton si tu veux
    const url = `/api/weather/stats?date=${encodeURIComponent(date)}&address=${encodeURIComponent(address)}&duration=${duration}`;

    fetch(url)
        .then(response => {
            if (!response.ok) throw new Error("Erreur météo");
            return response.json();
        })
        .then(data => {
            const img = document.getElementById('weather-img');
            const hiddenInput = document.getElementById('weather-icon-hidden');

            if (data.weatherIndicator) {
                img.src = `/images/weather/${data.weatherIndicator}.png`;
                hiddenInput.value = data.weatherIndicator;
            } else {
                img.src = '/images/weather/unknown.png';
                hiddenInput.value = '';
            }

            // Mise à jour des autres champs
            document.getElementById('weather-temp').value = data.averageTemperature || '--';
            document.getElementById('weather-wind').value = data.averageWindSpeed || '--';
            document.getElementById('weather-precip').value = data.averagePrecipitation || '--';

            // Et n'oublie pas les champs cachés techniques
            document.getElementById('weather-max').value = data.maxTemperature || '';
            document.getElementById('weather-min').value = data.minTemperature || '';
            document.getElementById('weather-apparent').value = data.averageApparentTemperature || '';
        })
        .catch(error => {
            console.error("Erreur:", error);
            alert("Impossible de récupérer la météo. Vérifiez l'adresse.");
        });
}

function detectCityFromBrowserLocation() {
    const addressInput = document.getElementById('address');
    if (!addressInput || addressInput.value.trim() !== '' || !navigator.geolocation) {
        return;
    }

    navigator.geolocation.getCurrentPosition(
        async (position) => {
            const latitude = position.coords.latitude;
            const longitude = position.coords.longitude;

            try {
                const response = await fetch(`/api/weather/reverse-city?lat=${encodeURIComponent(latitude)}&lon=${encodeURIComponent(longitude)}`);
                if (!response.ok) {
                    return;
                }
                const data = await response.json();
                if (data && data.city && typeof data.city === 'string') {
                    addressInput.value = data.city;
                }
            } catch (error) {
                console.warn("Géolocalisation ville impossible:", error);
            }
        },
        () => {
            // Ignore refus/erreur utilisateur, on laisse la saisie manuelle.
        },
        { enableHighAccuracy: false, timeout: 7000, maximumAge: 300000 }
    );
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', detectCityFromBrowserLocation);
} else {
    detectCityFromBrowserLocation();
}
