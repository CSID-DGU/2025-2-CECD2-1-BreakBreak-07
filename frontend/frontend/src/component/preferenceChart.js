import React from "react";
import { Radar } from "react-chartjs-2";

import {
  Chart as ChartJS,
  RadialLinearScale,
  PointElement,
  LineElement,
  Filler,
  Tooltip,
  Legend,
} from "chart.js";

ChartJS.register(
  RadialLinearScale,
  PointElement,
  LineElement,
  Filler,
  Tooltip,
  Legend
);

export default function PreferenceChart({ stats }) {
  const safeStats = {
    family: stats?.avgFamily ?? 0,
    friendship: stats?.avgFriendship ?? 0,
    adventure: stats?.avgAdventure ?? 0,
    growth: stats?.avgGrowth ?? 0,
  };

  const data = {
    labels: ["가족", "우정", "모험", "성장"],
    datasets: [
      {
        label: "주제 선호도",
        data: [
          safeStats.family,
          safeStats.friendship,
          safeStats.adventure,
          safeStats.growth,
        ],
        backgroundColor: "rgba(255, 180, 85, 0.15)",
        borderColor: "rgba(94, 124, 94, 0.7)",
        borderWidth: 2,
        pointBackgroundColor: "#5E7C5E",
        pointRadius: 3,
      },
    ],
  };

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { display: false } },
    scales: {
      r: {
        suggestedMin: 0,
        suggestedMax: 5,
        ticks: { display: false },
        grid: { color: "#ddd" },
        pointLabels: {
          color: "#5E7C5E",
          font: { 
            weight: "600",
            size: 15,
            family: "HakgyoansimGeurimilgi, Noto Sans KR, sans-serif",
          },
          padding: 9,
        },
      },
    },
  };

  return (
    <div className="preferenceChartContainer">
      <Radar data={data} options={options} />

      {!stats && (
        <p className="preferenceChartEmpty">아직 충분한 데이터가 없어요</p>
      )}
    </div>
  );
}
