/**
 * Pathshala Connect – पाठशाळा कनेक्ट
 * Client-side script for bilingual switching, mobile navigation, and interactive UI
 */

function getCookie(name) {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop().split(';').shift();
  return null;
}

function setCookie(name, value, days) {
  let expires = "";
  if (days) {
    const date = new Date();
    date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
    expires = "; expires=" + date.toUTCString();
  }
  document.cookie = name + "=" + (value || "") + expires + "; path=/";
}

// Current language detection
function getCurrentLang() {
  const urlParams = new URLSearchParams(window.location.search);
  const paramLang = urlParams.get('lang');
  if (paramLang && (paramLang === 'mr' || paramLang === 'en')) {
    localStorage.setItem('pc-lang', paramLang);
    setCookie('pathshala_lang', paramLang, 365);
    return paramLang;
  }
  const cookieLang = getCookie('pathshala_lang');
  if (cookieLang) return cookieLang;

  return localStorage.getItem('pc-lang') || 'mr';
}

function updateLangButtonDisplay() {
  const lang = getCurrentLang();
  const labelEl = document.getElementById('langCurrentLabel');
  if (labelEl) {
    labelEl.textContent = (lang === 'mr') ? 'English' : 'मराठी';
  }
}

function switchLanguage() {
  const current = getCurrentLang();
  const next = (current === 'mr') ? 'en' : 'mr';
  localStorage.setItem('pc-lang', next);
  setCookie('pathshala_lang', next, 365);

  const url = new URL(window.location.href);
  url.searchParams.set('lang', next);
  window.location.href = url.toString();
}

function toggleLanguage() {
  switchLanguage();
}

function togglePassword(inputId, btn) {
  const input = document.getElementById(inputId);
  if (!input) return;
  const isPw = input.type === 'password';
  input.type = isPw ? 'text' : 'password';
  if (btn) {
    btn.textContent = isPw ? 'लपवा' : 'दाखवा';
  }
}

function toggleMenu() {
  const nav = document.getElementById('mainNav');
  if (nav) {
    nav.classList.toggle('open');
  }
}

document.addEventListener('DOMContentLoaded', () => {
  updateLangButtonDisplay();
});
