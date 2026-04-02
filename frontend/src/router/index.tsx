import { createBrowserRouter } from "react-router-dom";
import App from "../App.tsx";
import Layout from "../shared/layout/layout.tsx";
import UserBookings from "../features/UserBookings/UserBookings.tsx";

const router = createBrowserRouter([
  {
    path: "/",
     element: <Layout />,
    children: [
       { index: true, element: <App />},
       { path: "my-bookings", element: <UserBookings />},
    
    ],
  },
  {
    path: "*",
    element: <div>Page Not Found</div>,
  }
]);

export default router;