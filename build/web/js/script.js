function changeSort(value) {
    window.location.href = `events-page?key=${currentKeyword}&publicFilter=${currentPublicFilter}&sortByDate=${value}`;
}
