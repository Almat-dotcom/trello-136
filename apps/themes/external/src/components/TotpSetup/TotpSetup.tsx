import React from "react";
import { KcContext } from "lib/kc";

type KcContextExtendedLoginConfigTotp = Extract<KcContext, { pageId: "login-config-totp.ftl" }>;

const TotpSetup = ({ kcContext }: { kcContext: KcContextExtendedLoginConfigTotp }) => {
    const { totp } = kcContext;

    if (!totp) {
        return <p>Configuration not available</p>;
    }

    return (
        <div className="totp-setup">
            <h1>Configure TOTP</h1>
            <p>Scan the QR code below with your authenticator app:</p>
            <img src={totp.totpSecretQrCode} alt="TOTP QR Code" />

            <p>If you cannot scan the QR code, use this manual code:</p>
            <code>{totp.totpSecret}</code>

            <a href={totp.manualUrl}>Click here to proceed</a>
        </div>
    );
};

export default TotpSetup;
