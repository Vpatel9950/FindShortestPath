const BASE = "http://localhost:8080/api/graph";
const tableBody = document.getElementById("resultTable");
const canvas = document.getElementById("graphCanvas");
const ctx = canvas.getContext("2d");

document.getElementById("runBtn").addEventListener("click", runDijkstra);

async function runDijkstra() {
  const source = document.getElementById("startVertex").value.trim() || "A";
  const destination = document.getElementById("endVertex").value.trim() || "I";

  tableBody.innerHTML = "<tr><td colspan='4'>Loading...</td></tr>";

  try {
    const response = await fetch(`${BASE}/shortest-path`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ start: source, end: destination, algorithm: "dijkstra" })
    });

    if (!response.ok) throw new Error("Server error: " + response.status);

    const data = await response.json();
    console.log("Response:", data);

    const path = data.path || data.result?.path;
    const distance = data.totalDistance || data.result?.totalDistance || data.distance;

    if (!Array.isArray(path)) {
      tableBody.innerHTML = `<tr><td colspan="4" style="color:red;">Error: Invalid path data</td></tr>`;
      return;
    }

    tableBody.innerHTML = "";
    path.forEach((node, i) => {
      const row = document.createElement("tr");
      row.innerHTML = `
        <td>${node}</td>
        <td>${i === 0 ? "Yes" : "No"}</td>
        <td>${i === path.length - 1 ? distance : "..."}</td>
        <td>${path.slice(0, i + 1).join(" → ")}</td>
      `;
      tableBody.appendChild(row);
    });

    drawGraph(path);
  } catch (err) {
    console.error(err);
    tableBody.innerHTML = `<tr><td colspan='4' style="color:red;">Error: ${err.message}</td></tr>`;
  }
}

function drawGraph(path) {
  ctx.clearRect(0, 0, canvas.width, canvas.height);

  const nodes = {
    A: { x: 450, y: 60 },
    B: { x: 300, y: 180 },
    C: { x: 450, y: 180 },
    D: { x: 600, y: 180 },
    E: { x: 450, y: 320 },
    F: { x: 250, y: 320 },
    G: { x: 650, y: 320 },
    H: { x: 350, y: 460 },
    I: { x: 550, y: 460 }
  };

  const edges = [
    ["A","B",4], ["A","C",2], ["B","C",5], ["B","F",7],["C","D",3],["C","E",3],["D","G",2],["E","H",4],["E","I",4],["F","I",2],["G","H",1],["H","I",3]
  ];

  ctx.lineWidth = 2;
  edges.forEach(([f, t, w]) => {
    const a = nodes[f], b = nodes[t];
    if (!a || !b) return;
    ctx.strokeStyle = "#bbb";
    ctx.beginPath();
    ctx.moveTo(a.x, a.y);
    ctx.lineTo(b.x, b.y);
    ctx.stroke();
    const mx = (a.x + b.x)/2, my = (a.y + b.y)/2;
    ctx.fillStyle = "#333";
    ctx.font = "12px Arial";
    ctx.fillText(w, mx+6, my-6);
  });

  ctx.lineWidth = 4;
  for (let i = 0; i < path.length - 1; i++) {
    const a = nodes[path[i]], b = nodes[path[i + 1]];
    if (!a || !b) continue;
    ctx.strokeStyle = "#28a745";
    ctx.beginPath();
    ctx.moveTo(a.x, a.y);
    ctx.lineTo(b.x, b.y);
    ctx.stroke();
  }

  Object.entries(nodes).forEach(([id, pos]) => {
    ctx.beginPath();
    ctx.arc(pos.x, pos.y, 22, 0, 2 * Math.PI);
    ctx.fillStyle = path.includes(id) ? "#28a745" : "#fff";
    ctx.fill();
    ctx.lineWidth = 2;
    ctx.strokeStyle = "#333";
    ctx.stroke();
    ctx.fillStyle = path.includes(id) ? "#fff" : "#000";
    ctx.font = "16px Arial";
    ctx.fillText(id, pos.x - 6, pos.y + 6);
  });
}
