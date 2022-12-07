import { KcContext } from "lib/kc";
import ReactMarkdown from "react-markdown";
import Close from "./close.svg";
import "./PdfModal.css";
import { useEffect, useState } from "react";

type PdfModalProps = {
    kcContext: KcContext
    url: string;
    shown: boolean;
    onClosed: () => void;
}

const PdfModal = ({ kcContext, url, shown, onClosed }: PdfModalProps) => {
    const [data, setData] = useState("");

    useEffect(() => {
        fetch(url).then(r => r.text()).then((data) => setData(data));
    }, [url])

    if (!shown || !data) {
        return (<></>)
    }

    return (
        <div
            id="default-modal"
            className="flex overflow-x-hidden overflow-y-auto fixed h-min-screen h-screen w-screen top-0 left-0 right-0 md:inset-0 z-50 justify-center items-center bg-gray-600 bg-opacity-40"
        >
            <div className="flex flex-col relative h-screen w-screen md:w-1/2 bg-white rounded-md shadow-md">
                <div className="relative w-full">
                    <button type="button" className="absolute right-0 w-6 h-6 mt-2 mr-8 rounded-full transition-colors hover:bg-gray-200" onClick={() => onClosed()}>
                        <img alt="close" src={Close} />
                    </button>
                </div>
                <div className="overflow-auto h-full w-full mt-4">
                    <ReactMarkdown className="reactMarkDown">{data}</ReactMarkdown>
                </div>
            </div>
        </div>
    );
}

export default PdfModal;
