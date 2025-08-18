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
window.addEventListener('DOMContentLoaded', function() {
    const isAdmin = document.body.dataset.isAdmin === "true";
    const isDesktop = window.innerWidth >= 992;
    if(isAdmin && isDesktop) {
        toggleNav();
    }
});
document.getElementById("closeSidebarBtn").onclick = function() {
    document.getElementById("mySidebar").classList.remove("show");
    document.getElementById("main").classList.remove("shifted");
};
function toTurkishLower(str) {
  return str.replace(/I/g, 'ı').replace(/İ/g, 'i').toLowerCase();

}
function toggleNav() {
  var sidebar = document.getElementById("mySidebar");
  var main = document.getElementById("main");
  sidebar.classList.toggle("show");
  main.classList.toggle("shifted");
}
const sortStates = {
  courseTable: { lastSortedCol: -1, sortDir: 'asc' },
  userTable: { lastSortedCol: -1, sortDir: 'asc' },
  examTable: { lastSortedCol: -1, sortDir: 'asc' }
};
function sortTable(tableId, colIndex) {
  const table = document.getElementById(tableId);
  if (!table) return;

  const tbody = table.tBodies[0];
  const rows = Array.from(tbody.rows);
  const ths = table.tHead.rows[0].cells;

  const state = sortStates[tableId];

  for (let i = 0; i < ths.length; i++) {
    let a = ths[i].querySelector('a');
    if (a) {
      a.innerHTML = a.innerText.replace(/[↑↓]/g, '').trim();
    }
  }

  if (state.lastSortedCol === colIndex) {
    state.sortDir = state.sortDir === 'asc' ? 'desc' : 'asc';
  } else {
    state.sortDir = 'asc';
    state.lastSortedCol = colIndex;
  }

  let a = ths[colIndex].querySelector('a');
  if (a) {
    a.innerHTML = a.innerText + (state.sortDir === 'asc' ? ' ↑' : ' ↓');
  }

  if (colIndex !== 0) {
    rows.sort((a, b) => {
      let valA = a.cells[colIndex].innerText.trim().toLowerCase();
      let valB = b.cells[colIndex].innerText.trim().toLowerCase();

      if (!isNaN(valA) && !isNaN(valB)) {
        valA = Number(valA);
        valB = Number(valB);
      }

      if (valA < valB) return state.sortDir === 'asc' ? -1 : 1;
      if (valA > valB) return state.sortDir === 'asc' ? 1 : -1;
      return 0;
    });
  }

  rows.forEach((row, idx) => {
    row.cells[0].innerText = idx + 1;
    tbody.appendChild(row);
  });
}
(function(){
  const table = document.getElementById('examTable');
  if (!table) return;

  const tbody = table.querySelector('tbody');
  const rows = () => Array.from(tbody.querySelectorAll('tr')).filter(r => !r.id);
  const searchInput = document.getElementById('searchInput');
  const courseFilter = document.getElementById('courseFilter');
  const typeFilter = document.getElementById('examTypeFilter');
  const fromFilter = document.getElementById('fromFilter');
  const toFilter = document.getElementById('toFilter');
  const noMsg = document.getElementById('noResultsMessage');
  const countEl = document.getElementById('examCount');

  function normalize(str){ return (str || '').toString().toLowerCase().trim(); }

  function applyFilters(){
    const q = normalize(searchInput && searchInput.value);
    const courseId = courseFilter && courseFilter.value;
    const typeId = typeFilter && typeFilter.value;
    const fromVal = fromFilter && fromFilter.value ? new Date(fromFilter.value) : null;
    const toVal = toFilter && toFilter.value ? new Date(toFilter.value) : null;

    let shown = 0;
    rows().forEach(tr => {
      const tds = tr.children;
      const hay = [
        tds[1]?.innerText,
        tds[2]?.innerText,
        tds[3]?.innerText,
        tds[4]?.innerText,
        tds[5]?.innerText,
        tds[8]?.innerText
      ].join(' ');
      const txtMatch = !q || normalize(hay).includes(q);
      const rowCourseOk = !courseId || tr.dataset.courseId === courseId;
      const rowTypeOk = !typeId || tr.dataset.typeId === typeId;

      const startStr = tr.dataset.start;
      const startDate = startStr ? new Date(startStr) : null;
      let dateOk = true;
      if (fromVal && startDate) dateOk = dateOk && (startDate >= fromVal);
      if (toVal && startDate) dateOk = dateOk && (startDate <= toVal);

      const visible = txtMatch && rowCourseOk && rowTypeOk && dateOk;
      tr.style.display = visible ? '' : 'none';
      if (visible) shown++;
    });

    if (noMsg) noMsg.style.display = shown === 0 ? '' : 'none';
    if (countEl) countEl.innerText = 'Toplam: ' + shown + ' sınav';
  }

  if (searchInput) searchInput.addEventListener('input', applyFilters);
  if (courseFilter) courseFilter.addEventListener('change', applyFilters);
  if (typeFilter) typeFilter.addEventListener('change', applyFilters);
  if (fromFilter) fromFilter.addEventListener('change', applyFilters);
  if (toFilter) toFilter.addEventListener('change', applyFilters);

  window.sortTable = function(tableId, colIndex){
    const table = document.getElementById(tableId);
    const tbody = table.tBodies[0];
    const arr = Array.from(tbody.querySelectorAll('tr')).filter(r => !r.id && r.style.display !== 'none');
    const currentDir = table.getAttribute('data-sort-dir-' + colIndex) === 'asc' ? 'desc' : 'asc';
    table.setAttribute('data-sort-dir-' + colIndex, currentDir);

    arr.sort((a, b) => {
      const A = a.children[colIndex]?.innerText.trim() || '';
      const B = b.children[colIndex]?.innerText.trim() || '';
      let cmp;
      if (!isNaN(parseFloat(A)) && !isNaN(parseFloat(B))) {
        cmp = parseFloat(A) - parseFloat(B);
      } else {
        cmp = A.localeCompare(B, 'tr', { sensitivity: 'base' });
      }
      return currentDir === 'asc' ? cmp : -cmp;
    });

    arr.forEach(tr => tbody.appendChild(tr));
  };

  document.addEventListener('DOMContentLoaded', applyFilters);
  applyFilters();
})();
document.addEventListener("DOMContentLoaded", function () {
  const toTrLower = (s) => (s||"").replace(/I/g,"ı").replace(/İ/g,"i").toLowerCase();
  const tokenize = (s) => toTrLower((s||"").trim()).split(/\s+/).filter(Boolean);

  function highlightStarts(cell, words) {
    if (!cell) return;
    if (!cell.dataset.original) cell.dataset.original = cell.textContent;
    let text = cell.dataset.original;
    if (!words.length) { cell.innerHTML = text; return; }

    const tokens = text.split(/\s+/).map(tok => {
      const low = toTrLower(tok);
      const w = words.find(w => low.startsWith(w));
      return w ? `<span class="highlight">${tok.substring(0, w.length)}</span>${tok.substring(w.length)}` : tok;
    });
    cell.innerHTML = tokens.join(' ');
  }

  function setupTableFilter(cfg) {
    const table = document.getElementById(cfg.tableId);
    if (!table) return;

    const tbody = table.tBodies[0];
    const rows = () => Array.from(tbody.querySelectorAll(cfg.rowSelector || "tr")).filter(r => !r.id);
    const countEl = cfg.countSelector ? document.querySelector(cfg.countSelector) : null;
    const noRow = cfg.noResultsRowId ? document.getElementById(cfg.noResultsRowId) : null;

    const searchEl = cfg.searchId ? document.getElementById(cfg.searchId) : null;
    const filterEls = (cfg.filters || []).map(f => ({
      el: document.getElementById(f.elId),
      key: f.dataKey,                 // satırdaki data-* anahtarı (dataset[key])
      match: f.match || ((sel, val) => sel === "" || sel == val) // eşleşme kuralı
    }));

    function apply() {
      const searchWords = tokenize(searchEl ? searchEl.value : "");
      let visible = 0;

      rows().forEach(tr => {
        if (tr.hasAttribute("data-deleted")) { tr.style.display="none"; return; }

        const nameCell = (typeof cfg.nameCellSelector === "function")
          ? cfg.nameCellSelector(tr)
          : tr.querySelector(cfg.nameCellSelector || "td:nth-child(2)");

        let ok = true;
        for (const f of filterEls) {
          if (!f.el) continue;
          const selVal = f.el.value;
          const rowVal = f.key ? tr.dataset[f.key] : null;
          if (!f.match(selVal, rowVal, tr)) { ok = false; break; }
        }

        if (ok && searchWords.length && nameCell) {
          const nameText = toTrLower(nameCell.textContent);
          const nameWords = nameText.split(/\s+/);
          ok = searchWords.every(w => nameWords.some(tok => tok.startsWith(w)));
        }

        tr.style.display = ok ? "" : "none";
        if (ok) {
          visible++;
          if (nameCell) highlightStarts(nameCell, searchWords);
        } else if (nameCell && nameCell.dataset.original) {
          nameCell.innerHTML = nameCell.dataset.original;
        }
      });

      if (noRow) noRow.style.display = (visible === 0 ? "table-row" : "none");
      if (countEl && cfg.countLabel) countEl.textContent = `${cfg.countLabel}: ${visible}`;
      else if (countEl) countEl.textContent = `Toplam: ${visible} kayıt`;
    }

    searchEl?.addEventListener("input", apply);
    filterEls.forEach(f => f.el?.addEventListener("change", apply));

    apply();
  }


  setupTableFilter({
    tableId: "courseTable",
    rowSelector: "tbody tr",
    nameCellSelector: ".course-name",
    countSelector: "#courseCount",
    countLabel: "Toplam",
    noResultsRowId: "noResultsMessage",
    searchId: "searchInput",
    filters: [
      { elId: "departmentFilter", dataKey: "department" },
      { elId: "semesterFilter", dataKey: "semester",
        match: (sel, rowVal) => {
          if (sel === "") return true;
          if (sel === "mesleki") return rowVal == "9";
          return sel == rowVal;
        }
      }
    ]
  });


  setupTableFilter({
    tableId: "examTable",
    rowSelector: "tbody tr",
    nameCellSelector: (tr) => tr.querySelector("td:nth-child(2)"),
    countSelector: "#examCount",
    noResultsRowId: "noResultsMessage",
    searchId: "searchInput",
    filters: [
      { elId: "departmentFilter", dataKey: "departmentId" },
      { elId: "examTypeFilter",  dataKey: "typeId" }
    ]
  });


  setupTableFilter({
    tableId: "userTable",
    rowSelector: "tbody tr",
    nameCellSelector: (tr) => tr.querySelector("td:nth-child(2)"),
    searchId: "searchInput"
  });

  const sortStates = {
    courseTable: { lastSortedCol: -1, sortDir: 'asc' },
    userTable:   { lastSortedCol: -1, sortDir: 'asc' },
    examTable:   { lastSortedCol: -1, sortDir: 'asc' }
  };
  window.sortTable = function(tableId, colIndex) {
    const table = document.getElementById(tableId);
    if (!table) return;
    const tbody = table.tBodies[0];
    const rows = Array.from(tbody.rows).filter(r => !r.id && r.style.display !== 'none'); // gizlenmişleri dışla
    const ths = table.tHead.rows[0].cells;
    const state = sortStates[tableId];

    for (let i = 0; i < ths.length; i++) {
      const a = ths[i].querySelector('a');
      if (a) a.innerHTML = a.innerText.replace(/[↑↓]/g, '').trim();
    }
    if (state.lastSortedCol === colIndex) state.sortDir = state.sortDir === 'asc' ? 'desc' : 'asc';
    else { state.sortDir = 'asc'; state.lastSortedCol = colIndex; }

    const a = ths[colIndex].querySelector('a');
    if (a) a.innerHTML = a.innerText + (state.sortDir === 'asc' ? ' ↑' : ' ↓');

    if (colIndex !== 0) {
      rows.sort((ra, rb) => {
        let va = ra.cells[colIndex]?.innerText.trim();
        let vb = rb.cells[colIndex]?.innerText.trim();
        const na = !isNaN(va) && va !== ""; const nb = !isNaN(vb) && vb !== "";
        if (na && nb) { va = Number(va); vb = Number(vb); }
        else { va = toTrLower(va); vb = toTrLower(vb); }
        if (va < vb) return state.sortDir === 'asc' ? -1 : 1;
        if (va > vb) return state.sortDir === 'asc' ? 1 : -1;
        return 0;
      });
    }
    rows.forEach((row, idx) => {
      if (row.cells[0]) row.cells[0].innerText = idx + 1;
      tbody.appendChild(row);
    });
  };
});
