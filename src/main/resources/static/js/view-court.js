/**
 * View Court Page - Booking Modal Functionality
 * Handles booking form interactions and validation
 */

    let pricePerHour = 0;

    /**
     * Calculate total booking cost based on duration
     */
    function calculateCost() {
        const startTime = document.getElementById('startTime');
        const endTime = document.getElementById('endTime');
        const totalCost = document.getElementById('totalCost');

        if (!startTime || !endTime || !totalCost) return;

        const startValue = startTime.value;
        const endValue = endTime.value;

        if (startValue && endValue && endValue > startValue) {
            const start = new Date('2000-01-01T' + startValue);
            const end = new Date('2000-01-01T' + endValue);
            const hours = (end - start) / (1000 * 60 * 60);
            const total = hours * pricePerHour;
            totalCost.textContent = 'EGP ' + total.toFixed(2);
        } else {
            totalCost.textContent = 'EGP 0.00';
        }
    }

    /**
     * Open booking modal
     */
    function openBookingModal() {
        const modal = document.getElementById('bookingModal');
        if (!modal) return;

        modal.classList.add('active');
        
        // Set minimum date to today
        const bookingDate = document.getElementById('bookingDate');
        if (bookingDate) {
            const today = new Date().toISOString().split('T')[0];
            bookingDate.min = today;
        }

        // Prevent body scroll when modal is open
        document.body.style.overflow = 'hidden';
    }

    /**
     * Close booking modal
     */
    function closeBookingModal() {
        const modal = document.getElementById('bookingModal');
        if (!modal) return;

        modal.classList.remove('active');
        
        // Restore body scroll
        document.body.style.overflow = '';
    }

    /**
     * Handle modal backdrop click
     * @param {Event} event
     */
    function handleBackdropClick(event) {
        if (event.target.id === 'bookingModal') {
            closeBookingModal();
        }
    }

    /**
     * Handle escape key press
     * @param {KeyboardEvent} event
     */
    function handleKeyDown(event) {
        if (event.key === 'Escape') {
            closeBookingModal();
        }
    }

    /**
     * Initialize booking form
     */
    function initBookingForm() {
        const startTime = document.getElementById('startTime');
        const endTime = document.getElementById('endTime');

        if (startTime) {
            startTime.addEventListener('change', calculateCost);
        }

        if (endTime) {
            endTime.addEventListener('change', calculateCost);
        }

        // Handle form submission
        const bookingForm = document.getElementById('bookingForm');
        if (bookingForm) {
            bookingForm.addEventListener('submit', function(e) {
                const start = document.getElementById('startTime').value;
                const end = document.getElementById('endTime').value;

                if (start >= end) {
                    e.preventDefault();
                    alert('End time must be after start time');
                    return false;
                }

                const bookingDate = document.getElementById('bookingDate').value;
                const today = new Date().toISOString().split('T')[0];

                if (bookingDate < today) {
                    e.preventDefault();
                    alert('Booking date cannot be in the past');
                    return false;
                }
            });
        }
    }

    /**
     * Auto-open modal on booking result
     */
    function handleBookingResult() {
        const urlParams = new URLSearchParams(window.location.search);
        const bookingStatus = urlParams.get('booking');

        if (bookingStatus === 'success' || bookingStatus === 'error') {
            openBookingModal();
        }
    }

    /**
     * Initialize when DOM is ready
     */
    function init() {
        // Get price from data attribute or global variable
        const priceElement = document.getElementById('bookingForm');
        if (priceElement) {
            const priceData = priceElement.dataset.pricePerHour;
            pricePerHour = parseFloat(priceData) || 0;
        }

        initBookingForm();
        handleBookingResult();

        // Expose functions globally for inline handlers
        window.openBookingModal = openBookingModal;
        window.closeBookingModal = closeBookingModal;
        window.handleBackdropClick = handleBackdropClick;
    }

    // Initialize on DOM load
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
