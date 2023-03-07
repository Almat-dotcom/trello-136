import { createRoot } from "react-dom/client";
import { StrictMode, lazy, Suspense } from "react";
import { kcContext } from "./KcApp/kcContext";

const KcApp = lazy(() => import("./KcApp"));

if (kcContext !== undefined) {
    console.log(kcContext);
}

createRoot(document.getElementById("root")!).render(
    <StrictMode>
        <Suspense>{kcContext === undefined ? <p>This is keycloak app! Please, provide keycloak context!</p> : <KcApp kcContext={kcContext} />}</Suspense>
    </StrictMode>,
);
