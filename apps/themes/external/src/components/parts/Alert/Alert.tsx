import { I18n } from "lib/i18n";

import CheckCircle from "./check_circle.svg";
import Warning from "./warning.svg";
import Report from "./report.svg";
import Info from "./info.svg";

const Alert = ({ type, message, i18n }: { type: "success" | "warning" | "error" | "info", message: string, i18n: I18n }) => {
    const { advancedMsgStr } = i18n;

    let bg = "bg-neutral-50";
    let color = "text-black";
    let border = "border-neutral-100";
    let icon = CheckCircle;
    switch (type) {
        case "success": {
            bg = "bg-green-100";
            color = "text-green-800";
            border = "border-green-800";
            icon = CheckCircle;
            break;
        }
        case "warning": {
            bg = "bg-yellow-100";
            color = "text-yellow-800";
            border = "border-yellow-800";
            icon = Warning;
            break;
        }
        case "error": {
            bg = "bg-red-100";
            color = "text-red-800";
            border = "border-red-800";
            icon = Report;
            break;
        }
        case "info": {
            bg = "bg-sky-100";
            color = "text-sky-800";
            border = "border-sky-800";
            icon = Info;
            break;
        }
    }

    const messages = message.split('<br>').map((it, i) => <span key={i} className="block font-bold">{advancedMsgStr(it) || it}</span>);

    return (
        <div className={`flex items-center px-2 py-3 ${bg} ${border} border-l-4 ${color} text-sm text-left`}>
            <img className="mr-2" alt="severity" src={icon} />
            <div>
                {messages}
            </div>
        </div>
    );
};

export default Alert;

