import { createBrowserRouter } from "react-router-dom";
import App from "../App.tsx";
import Layout from "../shared/layout/Layout.tsx";

const router = createBrowserRouter([
  {
    path: "/",
     element: <Layout />,
    children: [
       { index: true, element: <App /> },
    ],
  },
  {
    path: "*",
    element: <div>Page Not Found</div>,
  }
]);

export default router;