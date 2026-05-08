function toggleFilter(element) {
    element.classList.toggle('collapsed');
    const content = element.nextElementSibling;
    if (content) {
        content.classList.toggle('active');
    }
}

// Reset filters
function resetFilters() {
    // Clear search
    document.getElementById('searchInput').value = '';

    // Clear checkboxes
    document.querySelectorAll('.sport-checkbox, .type-checkbox').forEach(cb => cb.checked = false);

    // Reset price inputs
    document.querySelector('.min-input').value = '0';
    document.querySelector('.max-input').value = '10000';
    document.querySelector('.min-range').value = '0';
    document.querySelector('.max-range').value = '10000';

    // Update slider display
    updatePriceSlider();
}

// Color selection
document.querySelectorAll('.color-option').forEach(color => {
    color.addEventListener('click', function () {
        this.classList.toggle('selected');
    });
});

// Size selection
document.querySelectorAll('.size-option').forEach(size => {
    size.addEventListener('click', function () {
        this.classList.toggle('selected');
    });
});

// Style selection
document.querySelectorAll('.style-option').forEach(style => {
    style.addEventListener('click', function (e) {
        e.preventDefault();
        this.classList.toggle('selected');
    });
});
document.addEventListener('DOMContentLoaded', function () {
    const minInput = document.querySelector('.min-input');
    const maxInput = document.querySelector('.max-input');
    const minRange = document.querySelector('.min-range');
    const maxRange = document.querySelector('.max-range');

    minRange.addEventListener('input', updatePriceSlider);
    maxRange.addEventListener('input', updatePriceSlider);
    minInput.addEventListener('input', () => {
        minRange.value = minInput.value;
        updatePriceSlider();
    });
    maxInput.addEventListener('input', () => {
        maxRange.value = maxInput.value;
        updatePriceSlider();
    });

    // Initial update
    updatePriceSlider();
});
function updatePriceSlider() {
    const minInput = document.querySelector('.min-input');
    const maxInput = document.querySelector('.max-input');
    const minRange = document.querySelector('.min-range');
    const maxRange = document.querySelector('.max-range');
    const priceSlider = document.querySelector('.price-slider');

    let min = parseInt(minRange.value);
    let max = parseInt(maxRange.value);

    if (min > max) {
        [minRange.value, maxRange.value] = [maxRange.value, minRange.value];
        [min, max] = [max, min];
    }

    minInput.value = min;
    maxInput.value = max;

    if (priceSlider) {
        priceSlider.style.left = (min / 10000) * 100 + '%';
        priceSlider.style.right = (100 - (max / 10000) * 100) + '%';
    }
}