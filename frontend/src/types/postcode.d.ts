interface DaumPostcodeResult {
  zonecode: string;
  roadAddress: string;
  jibunAddress: string;
  userSelectedType: "R" | "J";
}

interface DaumPostcodeOptions {
  oncomplete: (data: DaumPostcodeResult) => void;
}

interface Window {
  daum?: {
    Postcode: new (options: DaumPostcodeOptions) => { open: () => void };
  };
}
