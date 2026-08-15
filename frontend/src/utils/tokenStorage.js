const TOKEN_KEY = 'ems_token';
const USER_KEY = 'ems_user';

export function getToken() {
  return sessionStorage.getItem(TOKEN_KEY);
}

export function getUser() {
  const raw = sessionStorage.getItem(USER_KEY);
  if (!raw) {
    return null;
  }

  try {
    return JSON.parse(raw);
  } catch {
    clearAuth();
    return null;
  }
}

export function setAuth(token, user) {
  sessionStorage.setItem(TOKEN_KEY, token);
  sessionStorage.setItem(USER_KEY, JSON.stringify(user));
}

export function clearAuth() {
  sessionStorage.removeItem(TOKEN_KEY);
  sessionStorage.removeItem(USER_KEY);
}

export function hasToken() {
  return Boolean(getToken());
}
