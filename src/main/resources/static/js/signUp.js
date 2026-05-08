function validateName(input) {
    const name = input.value.trim();
    const namePattern = /^[a-zA-Z\s]{2,}$/;
    
    if (name === '') {
        showError('nameError', 'Full name is required.');
        input.classList.add('input-error');
        return false;
    } else if (name.length < 2) {
        showError('nameError', 'Name must be at least 2 characters long.');
        input.classList.add('input-error');
        return false;
    } else if (!namePattern.test(name)) {
        showError('nameError', 'Name can only contain letters and spaces.');
        input.classList.add('input-error');
        return false;
    } else {
        clearError('nameError');
        input.classList.remove('input-error');
        return true;
    }
}

function validateEmail(input) {
    const email = input.value.trim();
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    
    if (email === '') {
        showError('emailError', 'Email is required.');
        input.classList.add('input-error');
        return false;
    } else if (!emailPattern.test(email)) {
        showError('emailError', 'Please enter a valid email address.');
        input.classList.add('input-error');
        return false;
    } else {
        clearError('emailError');
        input.classList.remove('input-error');
        return true;
    }
}

function validatePassword(input) {
    const password = input.value;
    
    if (password === '') {
        showError('passwordError', 'Password is required.');
        input.classList.add('input-error');
        return false;
    } else if (password.length < 6) {
        showError('passwordError', 'Password must be at least 6 characters long.');
        input.classList.add('input-error');
        return false;
    } else {
        clearError('passwordError');
        input.classList.remove('input-error');
        return true;
    }
}

function validateRole(input) {
    const role = input.value;
    
    if (role === '') {
        showError('roleError', 'Please select a user type.');
        input.classList.add('input-error');
        return false;
    } else {
        clearError('roleError');
        input.classList.remove('input-error');
        return true;
    }
}

function showError(elementId, message) {
    const errorElement = document.getElementById(elementId);
    if (errorElement) {
        errorElement.textContent = message;
        errorElement.classList.add('show');
    }
}

function clearError(elementId) {
    const errorElement = document.getElementById(elementId);
    if (errorElement) {
        errorElement.textContent = '';
        errorElement.classList.remove('show');
    }
}

function validateSignup(event) {
    event.preventDefault();
    
    const nameInput = document.getElementById('name');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const roleInput = document.getElementById('role');
    
    const isNameValid = validateName(nameInput);
    const isEmailValid = validateEmail(emailInput);
    const isPasswordValid = validatePassword(passwordInput);
    const isRoleValid = validateRole(roleInput);
    
    if (isNameValid && isEmailValid && isPasswordValid && isRoleValid) {
        // All validations passed, submit the form
        document.getElementById('signupForm').submit();
        return false;
    } else {
        // Validation failed, don't submit
        return false;
    }
}