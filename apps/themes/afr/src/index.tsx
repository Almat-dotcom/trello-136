import { createRoot } from "react-dom/client";
import { kcContext } from "./lib/kcContext";
import KcApp from "./components/KcApp";

createRoot(document.getElementById("root")!).render(
    <KcApp kcContext={kcContext!!}/>
);
