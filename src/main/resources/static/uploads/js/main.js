window.addEventListener('DOMContentLoaded', function() {
    const isAdmin = document.body.dataset.isAdmin === "true";
    const isDesktop = window.innerWidth >= 992;
    if(isAdmin && isDesktop) {
        toggleNav();
    }
    const searchInput = document.getElementById('searchInput');
    const tableRows = document.querySelectorAll('table tbody tr');

    searchInput.addEventListener('input', function () {
      const filter = toTurkishLower(searchInput.value.trim());
      const filterWords = filter.split(/\s+/).filter(w => w.length > 0);

      tableRows.forEach(row => {
        const nameCell = row.querySelector('td:nth-child(2) span');
        if (!nameCell) return;

        const originalName = nameCell.textContent;
        const nameLower = toTurkishLower(originalName);
        const nameWords = nameLower.split(/\s+/);

        const matches = filterWords.every(fw =>
          nameWords.some(nw => nw.startsWith(fw))
        );

        if (!matches) {
          row.style.display = 'none';
          nameCell.innerHTML = originalName;
          return;
        }

        row.style.display = '';

        // Highlight işlemi: sadece kelimelerin başındaki eşleşen kısmı işaretle
        let highlightedName = originalName;

        filterWords.forEach(word => {
          const originalWords = highlightedName.split(/\s+/);
          highlightedName = originalWords.map(ow => {
            const owLower = toTurkishLower(ow);
            if (owLower.startsWith(word)) {
              return `<span class="highlight">${ow.substring(0, word.length)}</span>` + ow.substring(word.length);
            } else {
              return ow;
            }
          }).join(' ');
        });

        nameCell.innerHTML = highlightedName;
      });
    });
});
function toTurkishLower(str) {
  return str.replace(/I/g, 'ı').replace(/İ/g, 'i').toLowerCase();
}
function toggleNav() {
  var sidebar = document.getElementById("mySidebar");
  var main = document.getElementById("main");
  sidebar.classList.toggle("show");
  main.classList.toggle("shifted");
}
document.getElementById("closeSidebarBtn").onclick = function() {
    document.getElementById("mySidebar").classList.remove("show");
    document.getElementById("main").classList.remove("shifted");
};
document.addEventListener('DOMContentLoaded', function () {
  const phoneInput = document.getElementById('phoneNumber');

  function formatPhoneNumber(e) {
    let value = (typeof e === "string" ? e : e.target.value).replace(/\D/g, '');

    if (!value.startsWith('0')) {
      value = '0' + value;
    }
    let formatted = value.substring(0, 1);
    if (value.length > 1) formatted += ' (' + value.substring(1, 4);
    if (value.length >= 4) formatted += ') ' + value.substring(4, 7);
    if (value.length >= 7) formatted += ' ' + value.substring(7, 9);
    if (value.length >= 9) formatted += ' ' + value.substring(9, 11);

    if (typeof e === "string") return formatted;
    e.target.value = formatted;
  }

  if (phoneInput && phoneInput.value) {
    phoneInput.value = formatPhoneNumber(phoneInput.value);
  }

  if (phoneInput) {
    phoneInput.addEventListener('input', formatPhoneNumber);

    phoneInput.form?.addEventListener('submit', function () {
      phoneInput.value = phoneInput.value.replace(/\D/g, '');
    });
  }
});

