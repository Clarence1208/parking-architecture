import { Outlet } from "react-router-dom";
import "./layout.css"
import Sidebar from "../ui/SideBar";
import { useAuth } from "../../store/AuthContext";
import { AuthPage } from "../../features/auth/AuthPage";

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
