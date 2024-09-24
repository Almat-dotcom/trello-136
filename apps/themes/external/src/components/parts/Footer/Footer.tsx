import type { I18n } from "lib/i18n";
import Network from "./network.svg";
import Document from "./document.svg";
import Phone from './phone.svg';
import Mail from './mail.svg';

const Footer = ({ i18n }: { i18n: I18n }) => {
    const { msgStr } = i18n;

    const currentYear = new Date().getUTCFullYear();

    return (
        <div className="flex flex-col-reverse md:flex-row items-center md:items-stretch md:justify-between w-full px-1 md:px-8 lg:px-24 xl:px-44 py-8 md:py-12 bg-secondary-background">
            <div className="flex flex-col justify-between md:w-auto">
                <div className="text-sm">
                    <a className="flex justify-center my-1 py-1.5 border border-dark-text hover:border-primary-focus rounded-sm text-center text-dark-text hover:text-primary-focus" href="https://kacd.kz">
                        <img className="inline-block mr-2" alt="net" src={Network}/>{msgStr("goToSite")}
                    </a>
                    <a className="flex justify-center my-1 py-1.5 border border-dark-text hover:border-primary-focus rounded-sm text-center text-dark-text hover:text-primary-focus" href="https://cabinet.kacd.kz/checkdoc">
                        <img className="inline-block mr-2" alt="net" src={Document}/>{msgStr("checkDoc")}
                    </a>
                </div>
                <div>
                    <span className="text-gray-500 text-sm">{"© " + msgStr("copyright") + currentYear }</span>
                </div>
            </div>
            <div className="w-64 md:w-auto pb-4 md:pb-0">
                <div className="mb-4 font-bold text-dark-text">{msgStr("contacts")}</div>
                <ul className="text-dark-text">
                    <li className="flex items-center justify-start mb-1">
                        <img className="inline-block mr-2" alt="net" src={Phone}/><a className="hover:underline focus:underline" href="tel:+7(727)262-08-46">+7 (727) 262 08 46</a>
                    </li>
                    <li className="flex items-center justify-start mb-1">
                        <img className="inline-block mr-2" alt="net" src={Phone}/><a className="hover:underline focus:underline" href="tel:+7(727)355-47-60">+7 (727) 355 47 60</a>
                    </li>
                    <li className="flex items-center justify-start mb-1">
                        <img className="inline-block mr-2" alt="net" src={Mail}/><a className="hover:underline focus:underline" href="mailto:helpdesk@kacd.kz">helpdesk@kacd.kz</a>
                    </li>
                </ul>
            </div>
            <div className="w-64 md:w-auto pb-4 md:pb-0">
                <div className="mb-4 font-bold text-dark-text">{msgStr("address")}</div>
                <div className="text-dark-text">
                    <p>050040, Алматы, </p>
                    <p>ул. Сатпаева, 30/8, </p>
                    <p>нежилое помещение 163</p>
                </div>
            </div>
        </div>
    );
};

export default Footer;
