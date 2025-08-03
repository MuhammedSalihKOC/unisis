document.addEventListener("DOMContentLoaded", function () {

    const generateBtn = document.getElementById("generateEmailBtn");
    const nameInput = document.getElementById("name");
    const suggestionsContainer = document.getElementById("emailSuggestionsContainer");
    const suggestionsDiv = document.getElementById("emailSuggestions");
    const selectedEmail = document.getElementById("selectedEmail");
    const readonlyEmail = document.getElementById("readonlyEmail");

    if (generateBtn && nameInput && suggestionsContainer && suggestionsDiv && selectedEmail && readonlyEmail) {
        const replaceTurkishChars = (text) => {
            return text
                .toLowerCase()
                .replace(/ç/g, "c")
                .replace(/ğ/g, "g")
                .replace(/ı/g, "i")
                .replace(/ö/g, "o")
                .replace(/ş/g, "s")
                .replace(/ü/g, "u")
                .replace(/[^a-z0-9 ]/g, "");
        };

        generateBtn.addEventListener("click", function () {
            const rawName = nameInput.value.trim();
            const safeName = replaceTurkishChars(rawName);
            const parts = safeName.split(" ").filter(Boolean);

            if (parts.length < 2) {
                const warning = document.getElementById("nameWarning");
                if (warning) {
                    warning.style.display = "block";
                    warning.style.opacity = "1";

                    clearTimeout(warning._fadeTimeout);
                    clearTimeout(warning._hideTimeout);

                    warning._fadeTimeout = setTimeout(() => {
                        warning.style.opacity = "0";
                        warning._hideTimeout = setTimeout(() => {
                            warning.style.display = "none";
                        }, 600);
                    }, 2000);
                }
                return;
            }

            const domain = "@ogr.eskisehir.edu.tr";
            const dotted = parts.join(".");
            const underscored = parts.join("_");
            const firstLetter = parts[0][0];
            const rest = parts.slice(1).join("");
            const mergedInitial = firstLetter + rest;
            const initials = parts.map(p => p[0]).join("");

            const options = [
                `${dotted}${domain}`,
                `${underscored}${domain}`,
                `${mergedInitial}${domain}`,
                `${initials}${domain}`
            ];

            suggestionsDiv.innerHTML = options.map(email => `
                <div class="email-suggestion">${email}</div>
            `).join("");

            suggestionsContainer.classList.remove("d-none");

            const suggestionElements = document.querySelectorAll(".email-suggestion");
            suggestionElements.forEach(el => {
                el.addEventListener("click", () => {
                    suggestionElements.forEach(e => e.classList.remove("selected"));
                    el.classList.add("selected");
                    readonlyEmail.value = el.textContent;
                    selectedEmail.value = el.textContent;
                });
            });

            suggestionElements[0].classList.add("selected");
            readonlyEmail.value = options[0];
            selectedEmail.value = options[0];
        });

        const form = document.querySelector("form");
        if (form) {
            form.addEventListener("submit", function (e) {
                if (readonlyEmail.value.trim() === "") {
                    e.preventDefault();
                    alert("Lütfen bir e-posta seçiniz.");
                }
            });
        }
    }

    const phoneInput = document.getElementById('number');
    if (phoneInput) {
        phoneInput.addEventListener('input', formatPhoneNumber);
        phoneInput.form?.addEventListener('submit', function () {
            phoneInput.value = phoneInput.value.replace(/\D/g, '');
        });
    }

    function formatPhoneNumber(e) {
        let value = e.target.value.replace(/\D/g, '');
        if (!value.startsWith('0')) {
            value = '0' + value;
        }
        let formatted = value.substring(0, 1);
        if (value.length > 1) formatted += ' (' + value.substring(1, 4);
        if (value.length >= 4) formatted += ') ' + value.substring(4, 7);
        if (value.length >= 7) formatted += ' ' + value.substring(7, 9);
        if (value.length >= 9) formatted += ' ' + value.substring(9, 11);
        e.target.value = formatted;
    }

    const modalElement = document.getElementById('courseDetailModal');
    if (modalElement) {
        const modal = new bootstrap.Modal(modalElement);
        document.querySelectorAll('.view-details').forEach(button => {
            button.addEventListener('click', function () {
                console.log('Detay butonuna tıklandı');
                document.getElementById('modal-code').textContent = this.dataset.code;
                document.getElementById('modal-name').textContent = this.dataset.name;
                document.getElementById('modal-department').textContent = this.dataset.department;
                document.getElementById('modal-instructor').textContent = this.dataset.instructor;
                document.getElementById('modal-credit').textContent = this.dataset.credit;
                document.getElementById('modal-capacity').textContent = this.dataset.capacity;
                document.getElementById('modal-semester').textContent = this.dataset.semester;
                modal.show();
            });
        });
    }
});
