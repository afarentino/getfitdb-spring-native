document.addEventListener('DOMContentLoaded', init, false);

let data, table, sortCol;
let sortAsc = false;

async function init() {
  
  // Select the table (well, tbody)
  table = document.querySelector('#catTable tbody');
  // get the cats
  let resp = await fetch('https://www.raymondcamden.com/.netlify/functions/get-cats');
  data = await resp.json();
  renderTable();
  
  // listen for sort clicks
  document.querySelectorAll('#catTable thead tr th').forEach(t => {
     t.addEventListener('click', sort, false);
  });
  
}

function renderTable() {
  // create html
  let result = '';
  data.forEach(c => {
     result += `<tr>
     <td>${c.name}</td>
     <td>${c.age}</td>
     <td>${c.breed}</td>
     <td>${c.gender}</td>
     </tr>`;
  });
  table.innerHTML = result;
}

function sort(e) {
  let thisSort = e.target.dataset.sort;
  if(sortCol === thisSort) sortAsc = !sortAsc;
  sortCol = thisSort;
  data.sort((a, b) => {
    if(a[sortCol] < b[sortCol]) return sortAsc?1:-1;
    if(a[sortCol] > b[sortCol]) return sortAsc?-1:1;
    return 0;
  });
  renderTable();
}