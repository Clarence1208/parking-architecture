import { Outlet } from "react-router-dom";
import "./layout.css"
import Sidebar from "../ui/SideBar";

export default function Layout() {
  return (
    <div className="layout">
      <Sidebar />
      <main className="layout__main">
        <Outlet />
      </main>
    </div>
  );
}
