/**
 * validation.js - Client-side form validation
 *
 * GROUP A REQUIREMENT: JavaScript for form validation
 *
 * HOW IT WORKS:
 *   - Intercepts form submit event
 *   - Validates each field before the form reaches the server
 *   - Shows inline error messages without a page reload
 *   - Server ALSO validates (never trust client alone)
 *
 * FUNCTIONS:
 *   validateRegisterForm()  - registration page
 *   validateLoginForm()     - login page
 *   validateCompForm()      - post competition (organizer)
 */

/* ===================== REGISTER FORM ===================== */
function validateRegisterForm() {
    const form = document.getElementById('registerForm');
    if (!form) return;

    form.addEventListener('submit', function(e) {
        let valid = true;

        // Name: required, min 2 chars
        const name = document.getElementById('name');
        valid = validateField(name, name.value.trim().length >= 2,
            'Name must be at least 2 characters') && valid;

        // Email: required, valid format
        const email = document.getElementById('email');
        const emailRegex = /^[\w.-]+@[\w.-]+\.[a-zA-Z]{2,}$/;
        valid = validateField(email, emailRegex.test(email.value.trim()),
            'Enter a valid email address') && valid;

        // Password: min 6 chars
        const password = document.getElementById('password');
        valid = validateField(password, password.value.length >= 6,
            'Password must be at least 6 characters') && valid;

        // Confirm password: must match
        const confirm = document.getElementById('confirmPassword');
        valid = validateField(confirm, confirm.value === password.value,
            'Passwords do not match') && valid;

        // College: required
        const college = document.getElementById('college');
        valid = validateField(college, college.value.trim().length > 0,
            'College name is required') && valid;

        if (!valid) {
            e.preventDefault(); // stop form submission if errors exist
        }
    });
}

/* ===================== LOGIN FORM ===================== */
function validateLoginForm() {
    const form = document.getElementById('loginForm');
    if (!form) return;

    form.addEventListener('submit', function(e) {
        let valid = true;

        const email = document.getElementById('email');
        valid = validateField(email, email.value.trim().length > 0,
            'Email is required') && valid;

        const password = document.getElementById('password');
        valid = validateField(password, password.value.length > 0,
            'Password is required') && valid;

        if (!valid) e.preventDefault();
    });
}

/* ===================== POST COMPETITION FORM ===================== */
function validateCompForm() {
    const form = document.getElementById('compForm');
    if (!form) return;

    form.addEventListener('submit', function(e) {
        let valid = true;

        const title = document.getElementById('title');
        valid = validateField(title, title.value.trim().length >= 5,
            'Title must be at least 5 characters') && valid;

        const description = document.getElementById('description');
        valid = validateField(description, description.value.trim().length >= 20,
            'Description must be at least 20 characters') && valid;

        const lastDate = document.getElementById('lastDate');
        const today    = new Date().toISOString().split('T')[0];
        valid = validateField(lastDate, lastDate.value >= today,
            'Last date must be today or a future date') && valid;

        const minTeam = parseInt(document.getElementById('teamSizeMin').value);
        const maxTeam = parseInt(document.getElementById('teamSizeMax').value);
        const maxEl   = document.getElementById('teamSizeMax');
        valid = validateField(maxEl, maxTeam >= minTeam,
            'Max team size must be >= min team size') && valid;

        if (!valid) e.preventDefault();
    });
}

/* ===================== HELPER ===================== */
/**
 * validateField - shows or hides the inline error for one input.
 * @param {HTMLElement} input  - the input element
 * @param {boolean}     isValid - result of your validation check
 * @param {string}      message - error message to show if invalid
 * @returns {boolean}   isValid (for chaining with &&)
 */
function validateField(input, isValid, message) {
    const errorEl = input.parentElement.querySelector('.field-error');

    if (!isValid) {
        input.classList.add('error');
        if (errorEl) {
            errorEl.textContent = message;
            errorEl.style.display = 'block';
        }
    } else {
        input.classList.remove('error');
        if (errorEl) errorEl.style.display = 'none';
    }
    return isValid;
}

/* ===================== REAL-TIME FEEDBACK ===================== */
// As user types in password, check confirm field live
document.addEventListener('DOMContentLoaded', function() {
    const confirmInput = document.getElementById('confirmPassword');
    const passwordInput = document.getElementById('password');

    if (confirmInput && passwordInput) {
        confirmInput.addEventListener('input', function() {
            const match = confirmInput.value === passwordInput.value;
            validateField(confirmInput, match, 'Passwords do not match');
        });
    }

    // Initialize all form validators
    validateRegisterForm();
    validateLoginForm();
    validateCompForm();
});
