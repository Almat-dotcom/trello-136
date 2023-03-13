const URL = 'wss://127.0.0.1:13579/';
const SIGN_TYPE_AUTH = 'AUTHENTICATION';
const STORAGE_TYPE_FS = 'PKCS12';
const METHOD = "signXml";

export class EDSError extends Error {
    constructor(public readonly reason: string) {
        super(`EDS sing failed! Reason: ${reason}`);
        Object.setPrototypeOf(this, EDSError.prototype);
        this.name = 'EDSError';
    }
}

export const ConnectionLost = new EDSError('Connection has been lost!');
export const CancelledByUser = new EDSError('Action has been cancelled by user!');
export const FailedToSign = new EDSError('Failed to sign auth!');

const signAuthXml = async (xml: string) => {
    return await callNCALayer({
        module: 'kz.gov.pki.knca.commonUtils',
        method: METHOD,
        args: [STORAGE_TYPE_FS, SIGN_TYPE_AUTH, xml, '', '']
    });
};

const callNCALayer = (request: any): Promise<string> => {
    return new Promise<string>((resolve, reject) => {
        const ws = new WebSocket(URL);

        let expectClose = false;
        const send = sendMessage(request, ws);

        ws.addEventListener('open', () => {
            send();
        });

        ws.addEventListener('close', () => {
            if (!expectClose) {
                reject(ConnectionLost);
            }
        });

        ws.addEventListener('error', () => {
            reject(ConnectionLost);
        });

        ws.addEventListener('message', (messageEvent: MessageEvent) => {
            const { code, message, responseObject } = JSON.parse(messageEvent.data);

            if (!code) {
                return;
            }

            expectClose = true;
            ws.close();

            if (code === '500') {
                if (message === 'action.canceled') {
                    reject(CancelledByUser);
                    return;
                }
                
                reject(FailedToSign);
                return;
            }

            resolve(responseObject);
        });
    });
}

const sendMessage = (message: any, ws: WebSocket) => () => {
    ws.send(JSON.stringify(message));
}

export { signAuthXml };
