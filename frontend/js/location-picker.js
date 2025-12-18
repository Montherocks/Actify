// Location Picker Component for Event Creation
// Uses Leaflet.js for map functionality with HIGH ACCURACY GPS

let locationPickerMap = null;
let locationPickerMarker = null;
let selectedLocation = null;
let accuracyCircle = null;

// Initialize the location picker
function initLocationPicker(containerId, initialLat = null, initialLng = null) {
    const container = document.getElementById(containerId);
    if (!container) {
        console.error('Location picker container not found:', containerId);
        return null;
    }

    // Clear any existing map
    if (locationPickerMap) {
        locationPickerMap.remove();
        locationPickerMap = null;
        locationPickerMarker = null;
        accuracyCircle = null;
    }

    // Default center (India)
    const defaultLat = initialLat || 20.5937;
    const defaultLng = initialLng || 78.9629;
    const defaultZoom = initialLat ? 17 : 5;

    // Create map
    locationPickerMap = L.map(containerId, {
        center: [defaultLat, defaultLng],
        zoom: defaultZoom,
        zoomControl: true
    });

    // Add tile layer with high detail
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors',
        maxZoom: 19
    }).addTo(locationPickerMap);

    // Add initial marker if coordinates provided
    if (initialLat && initialLng) {
        addLocationMarker(initialLat, initialLng);
        selectedLocation = { lat: initialLat, lng: initialLng };
        updateLocationInputs();
    }

    // Click handler to set EXACT location
    locationPickerMap.on('click', function(e) {
        const lat = e.latlng.lat;
        const lng = e.latlng.lng;
        
        console.log('Map clicked - Exact coordinates:', lat, lng);
        
        // Remove old accuracy circle if exists
        if (accuracyCircle) {
            locationPickerMap.removeLayer(accuracyCircle);
            accuracyCircle = null;
        }
        
        addLocationMarker(lat, lng);
        selectedLocation = { lat: lat, lng: lng };
        updateLocationInputs();
        
        // Zoom in for precision
        if (locationPickerMap.getZoom() < 15) {
            locationPickerMap.setView([lat, lng], 17);
        }
        
        // Get address info
        reverseGeocode(lat, lng);
    });

    console.log('Location picker initialized successfully');
    return locationPickerMap;
}

// Add or update marker with exact position
function addLocationMarker(lat, lng) {
    const markerIcon = L.divIcon({
        className: 'location-picker-marker',
        iconSize: [40, 40],
        iconAnchor: [20, 40],
        html: `<div style="width:40px;height:40px;position:relative;">
            <div style="width:30px;height:30px;background:linear-gradient(135deg,#10b981,#059669);border:3px solid white;border-radius:50% 50% 50% 0;transform:rotate(-45deg);box-shadow:0 3px 10px rgba(0,0,0,0.3);position:absolute;top:0;left:5px;">
                <div style="width:10px;height:10px;background:white;border-radius:50%;position:absolute;top:50%;left:50%;transform:translate(-50%,-50%) rotate(45deg);"></div>
            </div>
        </div>`
    });

    if (locationPickerMarker) {
        locationPickerMarker.setLatLng([lat, lng]);
    } else {
        locationPickerMarker = L.marker([lat, lng], { 
            icon: markerIcon,
            draggable: true 
        }).addTo(locationPickerMap);

        // Handle marker drag for fine-tuning
        locationPickerMarker.on('dragend', function(e) {
            const pos = e.target.getLatLng();
            console.log('Marker dragged - Exact coordinates:', pos.lat, pos.lng);
            
            selectedLocation = { lat: pos.lat, lng: pos.lng };
            updateLocationInputs();
            reverseGeocode(pos.lat, pos.lng);
            
            // Update popup
            locationPickerMarker.setPopupContent(createPopupContent(pos.lat, pos.lng));
        });
    }

    // Add popup showing exact coordinates
    locationPickerMarker.bindPopup(createPopupContent(lat, lng)).openPopup();
}

function createPopupContent(lat, lng) {
    return `<div style="text-align:center;min-width:160px;">
        <strong style="color:#10b981;">📍 Event Location</strong>
        <div style="font-size:11px;color:#1e293b;margin-top:6px;font-family:monospace;background:#f1f5f9;padding:6px;border-radius:4px;">
            ${lat.toFixed(8)}<br>${lng.toFixed(8)}
        </div>
        <div style="font-size:10px;color:#94a3b8;margin-top:4px;">
            Drag marker to fine-tune
        </div>
    </div>`;
}

// Update hidden inputs with EXACT coordinates (full precision)
function updateLocationInputs() {
    if (!selectedLocation) return;

    const latInput = document.getElementById('latitude');
    const lngInput = document.getElementById('longitude');

    // Store FULL precision coordinates
    if (latInput) {
        latInput.value = selectedLocation.lat;
        console.log('Latitude set:', selectedLocation.lat);
    }
    if (lngInput) {
        lngInput.value = selectedLocation.lng;
        console.log('Longitude set:', selectedLocation.lng);
    }

    // Update display
    const coordDisplay = document.getElementById('selectedCoordinates');
    if (coordDisplay) {
        coordDisplay.innerHTML = `<span style="color:#10b981;font-weight:600;font-family:monospace;">${selectedLocation.lat.toFixed(8)}, ${selectedLocation.lng.toFixed(8)}</span>`;
    }
}

// Reverse geocode (only for convenience - coordinates are the real data)
async function reverseGeocode(lat, lng) {
    try {
        const response = await fetch(
            `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}&zoom=18&addressdetails=1`,
            { headers: { 'User-Agent': 'Actify Volunteer App' } }
        );
        const data = await response.json();

        if (data && data.address) {
            const address = data.address;
            
            // Only auto-fill empty fields
            const locationInput = document.getElementById('location');
            if (locationInput && !locationInput.value.trim()) {
                const parts = [];
                if (address.road) parts.push(address.road);
                if (address.suburb || address.neighbourhood) parts.push(address.suburb || address.neighbourhood);
                locationInput.value = parts.length > 0 ? parts.join(', ') : (data.display_name || '').split(',').slice(0, 2).join(', ').trim();
            }

            const cityInput = document.getElementById('city');
            if (cityInput && !cityInput.value.trim()) {
                cityInput.value = address.city || address.town || address.village || address.county || address.state_district || '';
            }

            const stateInput = document.getElementById('state');
            if (stateInput && !stateInput.value.trim()) {
                stateInput.value = address.state || '';
            }

            const addressDisplay = document.getElementById('selectedAddress');
            if (addressDisplay) {
                addressDisplay.textContent = data.display_name || '';
            }
        }
    } catch (error) {
        console.log('Reverse geocode (non-critical):', error.message);
    }
}

// Use current location with HIGH ACCURACY GPS
function useCurrentLocation() {
    if (!navigator.geolocation) {
        alert('Geolocation is not supported by your browser');
        return;
    }

    const btn = document.getElementById('useMyLocationBtn');
    if (btn) {
        btn.disabled = true;
        btn.innerHTML = '<i data-lucide="loader" class="animate-spin"></i> Getting GPS...';
        if (typeof lucide !== 'undefined') lucide.createIcons();
    }

    // Request HIGH ACCURACY GPS location
    navigator.geolocation.getCurrentPosition(
        (position) => {
            const lat = position.coords.latitude;
            const lng = position.coords.longitude;
            const accuracy = position.coords.accuracy; // in meters

            console.log('=== GPS LOCATION OBTAINED ===');
            console.log('Latitude:', lat);
            console.log('Longitude:', lng);
            console.log('Accuracy:', accuracy, 'meters');

            if (locationPickerMap) {
                // Remove old accuracy circle
                if (accuracyCircle) {
                    locationPickerMap.removeLayer(accuracyCircle);
                }

                // Set view with high zoom
                locationPickerMap.setView([lat, lng], 18);
                
                // Add marker at exact position
                addLocationMarker(lat, lng);
                selectedLocation = { lat: lat, lng: lng };
                updateLocationInputs();
                reverseGeocode(lat, lng);

                // Show accuracy circle (how precise the GPS is)
                accuracyCircle = L.circle([lat, lng], {
                    radius: Math.min(accuracy, 100), // Cap at 100m for display
                    color: '#3b82f6',
                    fillColor: '#3b82f6',
                    fillOpacity: 0.1,
                    weight: 2,
                    dashArray: '5, 5'
                }).addTo(locationPickerMap);
            }

            if (btn) {
                btn.disabled = false;
                const accuracyText = accuracy < 10 ? 'Excellent' : accuracy < 30 ? 'Good' : 'OK';
                btn.innerHTML = `<i data-lucide="check-circle"></i> Located (${accuracyText}: ±${Math.round(accuracy)}m)`;
                btn.style.background = '#10b981';
                btn.style.color = 'white';
                btn.style.borderColor = '#10b981';
                if (typeof lucide !== 'undefined') lucide.createIcons();
                
                setTimeout(() => {
                    btn.innerHTML = '<i data-lucide="navigation"></i> Use My Location';
                    btn.style.background = '';
                    btn.style.color = '';
                    btn.style.borderColor = '';
                    if (typeof lucide !== 'undefined') lucide.createIcons();
                }, 3000);
            }
        },
        (error) => {
            console.error('Geolocation error:', error);
            let errorMsg = 'Unable to get your location. ';
            
            switch(error.code) {
                case error.PERMISSION_DENIED:
                    errorMsg += 'Please allow location access in your browser.';
                    break;
                case error.POSITION_UNAVAILABLE:
                    errorMsg += 'Location unavailable. Try again outside.';
                    break;
                case error.TIMEOUT:
                    errorMsg += 'Timed out. Please try again.';
                    break;
                default:
                    errorMsg += 'Click on the map to set location.';
            }
            
            alert(errorMsg);
            
            if (btn) {
                btn.disabled = false;
                btn.innerHTML = '<i data-lucide="navigation"></i> Use My Location';
                if (typeof lucide !== 'undefined') lucide.createIcons();
            }
        },
        {
            enableHighAccuracy: true,  // Use GPS if available
            timeout: 20000,            // Wait up to 20 seconds
            maximumAge: 0              // Always get fresh position
        }
    );
}

// Get selected location
function getSelectedLocation() {
    return selectedLocation;
}

// Reset location picker
function resetLocationPicker() {
    selectedLocation = null;
    
    if (locationPickerMarker && locationPickerMap) {
        locationPickerMap.removeLayer(locationPickerMarker);
        locationPickerMarker = null;
    }
    
    if (accuracyCircle && locationPickerMap) {
        locationPickerMap.removeLayer(accuracyCircle);
        accuracyCircle = null;
    }
    
    const coordDisplay = document.getElementById('selectedCoordinates');
    if (coordDisplay) coordDisplay.textContent = 'Click on map to select';
    
    const addressDisplay = document.getElementById('selectedAddress');
    if (addressDisplay) addressDisplay.textContent = '';
    
    const latInput = document.getElementById('latitude');
    const lngInput = document.getElementById('longitude');
    if (latInput) latInput.value = '';
    if (lngInput) lngInput.value = '';
}

// Export functions
window.initLocationPicker = initLocationPicker;
window.useCurrentLocation = useCurrentLocation;
window.getSelectedLocation = getSelectedLocation;
window.resetLocationPicker = resetLocationPicker;
window.addLocationMarker = addLocationMarker;
