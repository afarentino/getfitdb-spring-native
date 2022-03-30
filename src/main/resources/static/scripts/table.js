// Event Listener to initialize read table on page load
document.addEventListener("DOMContentLoaded", init, false);


async function init() {
  // Select the table (well, tbody)
  let table = document.querySelector("#recTable tbody");
  // get the Records
  let url = "http://localhost:8080/records";  //TODO -> This may need to change
  let resp = await fetch(url);
  let data = await resp.json();

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
