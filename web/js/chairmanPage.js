const eventSearchInput = document.getElementById("eventSearchInput");
const eventStatusFilter = document.getElementById("eventStatusFilter");

function setupEventListeners() {
    if (eventSearchInput) {
        eventSearchInput.addEventListener("input", filterAndSearchEvents);
    }

    if (eventStatusFilter) {
        eventStatusFilter.addEventListener("change", filterAndSearchEvents);
    }
}

function filterAndSearchEvents() {
    const selectedStatus = eventStatusFilter ? eventStatusFilter.value : "";
    const searchTerm = eventSearchInput
        ? eventSearchInput.value.toLowerCase()
        : "";

    let filteredEvents = clubEvents;

    if (selectedStatus !== "") {
        filteredEvents = filteredEvents.filter(
            (event) => event.status === selectedStatus
        );
    }

    if (searchTerm !== "") {
        filteredEvents = filteredEvents.filter((event) =>
            event.name.toLowerCase().includes(searchTerm)
        );
    }

    renderClubEventsTable(filteredEvents);
}