/**
 * EV Charge - Frontend Interactive Scripts
 * Maps, Geolocation, Time Slot Booking, and Calculations
 */

document.addEventListener('DOMContentLoaded', function () {
    // ------------------------------------------------------------------------
    // 1. Browser Geolocation API
    // ------------------------------------------------------------------------
    const locateBtn = document.getElementById('btnLocateMe');
    if (locateBtn) {
        locateBtn.addEventListener('click', function () {
            if (!navigator.geolocation) {
                alert('Geolocation is not supported by your browser.');
                return;
            }

            locateBtn.disabled = true;
            locateBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span> Locating...';

            navigator.geolocation.getCurrentPosition(
                function (position) {
                    const lat = position.coords.latitude;
                    const lng = position.coords.longitude;

                    const userLatInput = document.getElementById('userLat');
                    const userLngInput = document.getElementById('userLng');
                    if (userLatInput && userLngInput) {
                        userLatInput.value = lat;
                        userLngInput.value = lng;
                    }

                    // Auto-submit search form if on stations page
                    const searchForm = document.getElementById('stationSearchForm');
                    if (searchForm) {
                        searchForm.submit();
                    } else {
                        locateBtn.innerHTML = '<i class="bi bi-geo-alt-fill text-success"></i> Located!';
                    }
                },
                function (error) {
                    locateBtn.disabled = false;
                    locateBtn.innerHTML = '<i class="bi bi-geo-alt-fill"></i> Near Me';
                    let msg = 'Unable to retrieve location.';
                    if (error.code === error.PERMISSION_DENIED) {
                        msg = 'Location access was denied. You can search manually by City or Locality.';
                    }
                    alert(msg);
                },
                { timeout: 10000, enableHighAccuracy: true }
            );
        });
    }

    // ------------------------------------------------------------------------
    // 2. Multi-Station Leaflet Map Initialization
    // ------------------------------------------------------------------------
    const mapElement = document.getElementById('stationMap');
    if (mapElement && typeof L !== 'undefined') {
        const rawStationsData = mapElement.getAttribute('data-stations');
        let stations = [];
        try {
            stations = JSON.parse(rawStationsData || '[]');
        } catch (e) {
            console.error('Error parsing stations data:', e);
        }

        // Default map center (Kannur, Kerala or first station)
        let defaultLat = 11.874477;
        let defaultLng = 75.370369;

        if (stations.length > 0 && stations[0].latitude && stations[0].longitude) {
            defaultLat = stations[0].latitude;
            defaultLng = stations[0].longitude;
        }

        const map = L.map('stationMap').setView([defaultLat, defaultLng], 10);

        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 19,
            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        }).addTo(map);

        // Custom EV Marker Icon
        const evIcon = L.divIcon({
            className: 'custom-ev-marker',
            html: '<div style="background-color:#0d1b2a; color:#00e676; border:2px solid #00e676; border-radius:50%; width:34px; height:34px; display:flex; align-items:center; justify-content:center; box-shadow:0 3px 6px rgba(0,0,0,0.3);"><i class="bi bi-lightning-charge-fill" style="font-size:18px;"></i></div>',
            iconSize: [34, 34],
            iconAnchor: [17, 34],
            popupAnchor: [0, -34]
        });

        const bounds = [];

        stations.forEach(function (st) {
            if (st.latitude && st.longitude) {
                const lat = parseFloat(st.latitude);
                const lng = parseFloat(st.longitude);
                bounds.push([lat, lng]);

                const marker = L.marker([lat, lng], { icon: evIcon }).addTo(map);

                const directionsUrl = `https://www.google.com/maps/dir/?api=1&destination=${lat},${lng}`;
                const detailsUrl = `/stations/${st.id}`;
                const bookingUrl = `/booking/new?stationId=${st.id}`;

                const popupContent = `
                    <div style="min-width: 220px; font-family: inherit;">
                        <h6 style="margin: 0 0 4px; font-weight: 700; color: #0d1b2a;">${st.name}</h6>
                        <p style="margin: 0 0 6px; font-size: 12px; color: #64748b;">${st.address}, ${st.city}</p>
                        <div style="margin-bottom: 8px;">
                            <span class="badge bg-success" style="font-size: 11px;">${st.status}</span>
                            ${st.demo ? '<span class="badge bg-warning text-dark" style="font-size: 10px;">DEMO DATA</span>' : ''}
                        </div>
                        <div style="display: flex; gap: 6px;">
                            <a href="${detailsUrl}" class="btn btn-sm btn-outline-dark" style="font-size: 11px; padding: 2px 8px;">Details</a>
                            <a href="${bookingUrl}" class="btn btn-sm btn-success" style="font-size: 11px; padding: 2px 8px; font-weight:600;">Book</a>
                            <a href="${directionsUrl}" target="_blank" class="btn btn-sm btn-primary" style="font-size: 11px; padding: 2px 8px;" title="Get Directions">
                                <i class="bi bi-geo-alt"></i>
                            </a>
                        </div>
                    </div>
                `;
                marker.bindPopup(popupContent);
            }
        });

        if (bounds.length > 1) {
            map.fitBounds(bounds, { padding: [40, 40] });
        }
    }

    // ------------------------------------------------------------------------
    // 3. Single Station Map
    // ------------------------------------------------------------------------
    const singleMapElement = document.getElementById('singleStationMap');
    if (singleMapElement && typeof L !== 'undefined') {
        const lat = parseFloat(singleMapElement.getAttribute('data-lat'));
        const lng = parseFloat(singleMapElement.getAttribute('data-lng'));
        const name = singleMapElement.getAttribute('data-name');

        if (!isNaN(lat) && !isNaN(lng)) {
            const map = L.map('singleStationMap').setView([lat, lng], 14);

            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                maxZoom: 19,
                attribution: '&copy; OpenStreetMap'
            }).addTo(map);

            const evIcon = L.divIcon({
                className: 'custom-ev-marker',
                html: '<div style="background-color:#0d1b2a; color:#00e676; border:2px solid #00e676; border-radius:50%; width:36px; height:36px; display:flex; align-items:center; justify-content:center;"><i class="bi bi-lightning-charge-fill" style="font-size:20px;"></i></div>',
                iconSize: [36, 36],
                iconAnchor: [18, 36],
                popupAnchor: [0, -36]
            });

            L.marker([lat, lng], { icon: evIcon }).addTo(map)
                .bindPopup(`<b>${name}</b><br><a href="https://www.google.com/maps/dir/?api=1&destination=${lat},${lng}" target="_blank">Get Directions</a>`)
                .openPopup();
        }
    }

    // ------------------------------------------------------------------------
    // 4. Interactive Time Slot Selection in Booking Form
    // ------------------------------------------------------------------------
    const slotPills = document.querySelectorAll('.slot-pill.available');
    const startTimeInput = document.getElementById('startTimeInput');
    const endTimeInput = document.getElementById('endTimeInput');
    const displayEstimatedEnergy = document.getElementById('displayEstimatedEnergy');
    const displayEstimatedAmount = document.getElementById('displayEstimatedAmount');
    const chargerPowerRate = parseFloat(document.getElementById('chargerPowerRate')?.value || '50');
    const chargerPriceRate = parseFloat(document.getElementById('chargerPriceRate')?.value || '18');

    slotPills.forEach(function (pill) {
        pill.addEventListener('click', function () {
            slotPills.forEach(p => p.classList.remove('selected'));
            this.classList.add('selected');

            const start = this.getAttribute('data-start');
            const end = this.getAttribute('data-end');

            if (startTimeInput && endTimeInput) {
                startTimeInput.value = start;
                endTimeInput.value = end;
            }

            // Calculate estimated energy (power * 1 hr * 0.85 typical EV factor)
            const energy = (chargerPowerRate * 0.85).toFixed(2);
            const amount = (energy * chargerPriceRate).toFixed(2);

            if (displayEstimatedEnergy) displayEstimatedEnergy.innerText = energy + ' kWh';
            if (displayEstimatedAmount) displayEstimatedAmount.innerText = '₹' + amount;
        });
    });
});
