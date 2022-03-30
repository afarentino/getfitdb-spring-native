// Table.js
// @see Source Derived from Raymond Camden's blog post at
// https://www.raymondcamden.com/2022/03/14/building-table-sorting-and-pagination-in-javascript 

// Event Listener to initialize read table on page load
document.addEventListener("DOMContentLoaded", init, false);

let data, table, sortCol;
let sortAsc = false;

async function init() {
  // Select the table (well, tbody)
  table = document.querySelector("#recTable tbody");
  // get the Records
  let url = "http://localhost:8080/records";  //TODO -> This may need to change
  let resp = await fetch(url);
  data = await resp.json();
  renderTable();
  
  // listen for sort clicks
  document.querySelectorAll('#recTable thead tr th').forEach(t => {
     t.addEventListener('click', sort, false);
  });
}

function renderTable() {
    // create html
    let result = "";
    data.forEach((c) => {
    result += `<tr>
                  <td>${c.start}</td>
                  <td>${c.distance}</td>
                  <td>${c.zoneTime}</td>
                  <td>${c.elapsedTime}</td>
                  <td>${c.caloriesBurned}</td>
                  <td>${c.avgHeartRate}</td>
                  <td>${c.maxHeartRate}</td>
                  <td>${c.notes}</td>
              </tr>`;
    });
    table.innerHTML = result;
}

function sort(e) {
    let thisSort = e.target.dataset.sort;
    if(sortCol === thisSort) sortAsc = !sortAsc;
    sortCol = thisSort;
    data.sort((a, b) => {
      if(a[sortCol] < b[sortCol]) return sortAsc ? 1 : -1;
      if(a[sortCol] > b[sortCol]) return sortAsc ? -1 : 1;
      return 0;
    });
    renderTable();
  }

