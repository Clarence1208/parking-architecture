import { createBrowserRouter } from "react-router-dom";
import App from "../App.tsx";

const router = createBrowserRouter([
  {
    path: "/",
    element: <App />,
  },
  {
    path: "*",
    element: <div>Page Not Found</div>,
  }
]);

export default router;