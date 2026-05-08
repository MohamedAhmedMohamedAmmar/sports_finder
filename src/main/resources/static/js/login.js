function validateEmail(input) {
    const email = input.value.trim();
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    
    if (email === '') {
        document.getElementById("emailError").textContent = "Email is required.";
        return false;
    } else if (!emailPattern.test(email)) {
        document.getElementById("emailError").textContent = "Please enter a valid email address.";
        return false;
    } else {
        document.getElementById("emailError").textContent = "";
        return true;
    }
}

function validatePassword(input) {
    const password = input.value;
    
    if (password === '') {
        document.getElementById("passwordError").textContent = "Password is required.";
        return false;
    } else if (password.length < 6) {
        document.getElementById("passwordError").textContent = "Password must be at least 6 characters long.";
        return false;
    } else {
        document.getElementById("passwordError").textContent = "";
        return true;
    }
}

function validateLogin(event) {
    event.preventDefault();
    
    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");
    
    const isEmailValid = validateEmail(emailInput);
    const isPasswordValid = validatePassword(passwordInput);
    
    if (isEmailValid && isPasswordValid) {
        // Form is valid, submit it
        document.getElementById("loginForm").submit();
        return false;
    } else {
        // Form is invalid, don't submit
        return false;
    }
}