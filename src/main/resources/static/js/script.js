document.addEventListener("DOMContentLoaded", () => {
  fetch("/api/analytics/department-count")
    .then(response => response.json())
    .then(data => {
      const departments = Object.keys(data);
      const counts = Object.values(data);

      const ctx = document.getElementById("departmentChart").getContext("2d");

      new Chart(ctx, {
        type: "bar",
        data: {
          labels: departments,
          datasets: [{
            label: "Number of Employees",
            data: counts,
            backgroundColor: [
              "#ff6b6b", "#6bc5d2", "#ffd93d", "#a29bfe", "#00b894", "#e17055", "#fd79a8"
            ],
            borderRadius: 10,
            borderWidth: 1
          }]
        },
        options: {
          responsive: true,
          plugins: {
            title: {
              display: true,
              text: "Employees per Department",
              font: { size: 20 }
            },
            legend: {
              display: false
            },
            tooltip: {
              callbacks: {
                label: function (context) {
                  return ` ${context.parsed.y} Employees`;
                }
              }
            }
          },
          scales: {
            y: {
              beginAtZero: true,
              ticks: {
                stepSize: 1
              }
            }
          }
        }
      });
    })
    .catch(error => {
      console.error("Error fetching chart data:", error);
    });
});
