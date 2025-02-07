import React, { memo } from "react";
import emblem from "../KcApp/emblem.png";
import { I18n } from "lib/i18n";

export const AfrHeader = memo(({ i18n }: { i18n: I18n }) => {
    const { msgStr } = i18n;

    return (
        <div id="kc-header" className="afr-header">
            <div className="afr-header-content">
                <img src={emblem} alt="Lock Icon" height="100px" />
                <p className="header-text">{msgStr("afrHeader")}</p>
            </div>
        </div>
    );
});
