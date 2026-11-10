const normalize = (value) => (value === null || value === undefined ? "" : String(value));

export const maskPhone = (value) => {
  const text = normalize(value);
  return text.length < 7 ? text : `${text.slice(0, 3)}****${text.slice(-4)}`;
};

export const maskEmail = (value) => {
  const text = normalize(value);
  const atIndex = text.indexOf("@");
  if (atIndex <= 0) return text;

  const name = text.slice(0, atIndex);
  return `${name.slice(0, 1)}${"*".repeat(Math.max(2, name.length - 1))}${text.slice(atIndex)}`;
};

export const maskIdCard = (value) => {
  const text = normalize(value);
  return text.length < 8 ? text : `${text.slice(0, 4)}********${text.slice(-4)}`;
};

export const maskBankCard = (value) => {
  const text = normalize(value);
  return text.length < 8 ? text : `${text.slice(0, 4)} **** **** ${text.slice(-4)}`;
};

export const maskAddress = (value) => {
  const text = normalize(value).trim();
  return text.length <= 6 ? text : `${text.slice(0, 6)}***`;
};
