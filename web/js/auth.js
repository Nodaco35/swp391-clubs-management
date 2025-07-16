// Auth.js - JavaScript cho trang đăng nhập và đăng ký

document.addEventListener("DOMContentLoaded", () => {
    const alerts = document.querySelectorAll(".alert")
    const forms = document.querySelectorAll("form")
    const emailInputs = document.querySelectorAll('input[type="email"]')
    const loginForm = document.querySelector(".auth-form")
    const registerForm = document.getElementById("registerForm")

    // Auto-hide alerts after 5 seconds
    alerts.forEach((alert) => {
        setTimeout(() => {
            alert.style.opacity = "0"
            setTimeout(() => {
                alert.style.display = "none"
            }, 300)
        }, 5000)
    })

    // Form submission with loading state
    forms.forEach((form) => {
        form.addEventListener("submit", function (e) {
            const submitBtn = this.querySelector(".submit-btn")

            // Show loading state
            submitBtn.classList.add("loading")
            submitBtn.disabled = true

            // Re-enable button after 5 seconds (fallback)
            setTimeout(() => {
                submitBtn.classList.remove("loading")
                submitBtn.disabled = false
            }, 5000)
        })
    })

    // Login form validation
    if (loginForm && !registerForm) {
        loginForm.addEventListener("submit", function (e) {
            const email = this.querySelector('input[name="email"]').value.trim()
            const password = this.querySelector('input[name="password"]').value.trim()

            if (!validateEmail(email)) {
                e.preventDefault()
                showAlert("Email không hợp lệ!", "error")
                this.querySelector('input[name="email"]').classList.add("error")
                return
            }
            if (password.length < 6) {
                e.preventDefault()
                showAlert("Mật khẩu phải có ít nhất 6 ký tự!", "error")
                this.querySelector('input[name="password"]').classList.add("error")
                return
            }
        })
    }

    // Register form validation
    if (registerForm) {
        registerForm.addEventListener("submit", (e) => {
            const password = document.querySelector('input[name="password"]').value
            const confirmPassword = document.querySelector('input[name="confirmPassword"]').value

            if (password !== confirmPassword) {
                e.preventDefault()
                showAlert("Mật khẩu xác nhận không khớp!", "error")
                return
            }

            if (password.length < 6) {
                e.preventDefault()
                showAlert("Mật khẩu phải có ít nhất 6 ký tự!", "error")
                return
            }
        })
    }

    // Email validation on blur
    emailInputs.forEach((input) => {
        input.addEventListener("blur", function () {
            const email = this.value.trim()
            if (email && !validateEmail(email)) {
                this.classList.add("error")
                showAlert("Email không hợp lệ!", "error")
            } else {
                this.classList.remove("error")
            }
        })
    })

    // Clear error class on page load if input is valid
    emailInputs.forEach((input) => {
        if (input.value && validateEmail(input.value)) {
            input.classList.remove("error")
        }
    })
})

// 🔧 FIX: Password toggle function - CHỈ DÙNG 1 FUNCTION NÀY
function togglePassword(inputId) {
    const input = document.getElementById(inputId)
    const icon = document.getElementById(inputId + "-icon")

    // Debug log
    console.log("Toggle password for:", inputId)
    console.log("Input found:", input)
    console.log("Icon found:", icon)

    if (input && icon) {
        if (input.type === "password") {
            input.type = "text"
            icon.className = "fas fa-eye-slash password-toggle"
            icon.setAttribute("aria-label", "Ẩn mật khẩu")
        } else {
            input.type = "password"
            icon.className = "fas fa-eye password-toggle"
            icon.setAttribute("aria-label", "Hiện mật khẩu")
        }
    } else {
        console.error("Cannot find input or icon for:", inputId)
    }
}

// Show alert function
function showAlert(message, type) {
    // Remove existing alerts
    const existingAlerts = document.querySelectorAll(".alert")
    existingAlerts.forEach((alert) => alert.remove())

    // Create new alert
    const alert = document.createElement("div")
    alert.className = `alert alert-${type}`
    alert.textContent = message

    // Insert alert before form
    const formSection = document.querySelector(".form-section")
    const form = document.querySelector(".auth-form")
    if (formSection && form) {
        formSection.insertBefore(alert, form)
    }

    // Auto-hide after 5 seconds
    setTimeout(() => {
        alert.style.opacity = "0"
        setTimeout(() => {
            alert.remove()
        }, 300)
    }, 5000)
}

// Email validation function
function validateEmail(email) {
    const re = /^[a-zA-Z0-9_+&*-]+(?:\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\.)+[a-zA-Z]{2,7}$/
    return re.test(email)
}

// Smooth scroll for navigation links
document.querySelectorAll('a[href^="#"]').forEach((anchor) => {
    anchor.addEventListener("click", function (e) {
        e.preventDefault()
        const target = document.querySelector(this.getAttribute("href"))
        if (target) {
            target.scrollIntoView({
                behavior: "smooth",
            })
        }
    })
})

// Mobile menu toggle
function toggleMobileMenu() {
    const navMenu = document.querySelector(".nav-menu")
    if (navMenu) {
        navMenu.classList.toggle("active")
    }
}

// Form input focus effects
document.querySelectorAll(".form-input").forEach((input) => {
    input.addEventListener("focus", function () {
        this.parentElement.classList.add("focused")
    })

    input.addEventListener("blur", function () {
        this.parentElement.classList.remove("focused")
    })
})

// Prevent form resubmission on page refresh
if (window.history.replaceState) {
    window.history.replaceState(null, null, window.location.href)
}

// Password show/hide toggle for reset/forgot password forms
function addPasswordToggle() {
    const pwFields = document.querySelectorAll('.fp-input[type="password"]');
    pwFields.forEach(function(input) {
        // Only add if not already present
        if (!input.parentElement.querySelector('.fp-toggle')) {
            const btn = document.createElement('button');
            btn.type = 'button';
            btn.className = 'fp-toggle';
            btn.innerHTML = '<i class="fas fa-eye"></i>';
            btn.style.position = 'absolute';
            btn.style.right = '12px';
            btn.style.top = '50%';
            btn.style.transform = 'translateY(-50%)';
            btn.style.background = 'none';
            btn.style.border = 'none';
            btn.style.cursor = 'pointer';
            btn.style.padding = '0 8px';
            btn.style.color = '#888';
            btn.onclick = function(e) {
                e.preventDefault();
                if (input.type === 'password') {
                    input.type = 'text';
                    btn.innerHTML = '<i class="fas fa-eye-slash"></i>';
                } else {
                    input.type = 'password';
                    btn.innerHTML = '<i class="fas fa-eye"></i>';
                }
            };
            input.parentElement.style.position = 'relative';
            input.parentElement.appendChild(btn);
        }
    });
}

// Hiệu ứng rung khi nhập sai hoặc có lỗi
function shakeOnError() {
    var err = document.querySelector('.fp-error');
    if (err) {
        var container = document.querySelector('.fp-container');
        if (container) {
            container.style.animation = 'fpShake 0.3s';
            container.addEventListener('animationend', function handler() {
                container.style.animation = '';
                container.removeEventListener('animationend', handler);
            });
        }
    }
}

document.addEventListener('DOMContentLoaded', function() {
    addPasswordToggle();
    // Smooth scroll to error/message if present
    var err = document.querySelector('.fp-error, .fp-message');
    if (err) {
        err.scrollIntoView({ behavior: 'smooth', block: 'center' });
        shakeOnError();
    }
});
