function fetchWeather() {
    const date = document.getElementById('date').value;
    const address = document.getElementById('address').value;
    const duration = document.getElementById('duration').value;

    if (!date || !address || !duration) {
        alert("Veuillez remplir la date, l'adresse et la durée pour obtenir la météo.");
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