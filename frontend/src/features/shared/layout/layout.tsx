import { Outlet } from "react-router-dom";
import "./layout.css"
import Sidebar from "../ui/SideBar.tsx";
import { useAuth } from "../../../store/AuthContext.tsx";
import { AuthPage } from "../../auth/AuthPage.tsx";

export default function Layout() {
  const { isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    return <AuthPage />;
  }

  return (
    <div className="layout">
      <Sidebar />
      <main className="layout__main">
        <Outlet />
      </main>
    </div>
  );
}
