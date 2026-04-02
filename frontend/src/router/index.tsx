import { createBrowserRouter } from "react-router-dom";
import App from "../App.tsx";
import Layout from "../features/shared/layout/layout.tsx";
import UserBookings from "../features/UserBookings/UserBookings.tsx";
import Dashboard from "../features/dashboard/Dashboard.tsx";
import CheckInPage from "../features/checkin/CheckInPage.tsx";
import QRCodesPage from "../features/qrcodes/QRCodesPage.tsx";

const router = createBrowserRouter([
  {
    path: "/",
     element: <Layout />,
    children: [
        { index: true, element: <App />},
        { path: "my-bookings", element: <UserBookings />},
        { path: "dashboard", element: <Dashboard /> },
        { path: "qr-codes", element: <QRCodesPage /> },
    ],
  },
  {
    path: "/check-in",
    element: <CheckInPage />,
  },
  {
    path: "*",
    element: <div>Page Not Found</div>,
  }
]);

export default router;