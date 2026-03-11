/**
 * FORMA Admin Panel — Shared JavaScript Utilities
 * Include this script on every admin page before the page-specific script.
 */

// ============================================================
// AUTH — check login and provide auth headers
// ============================================================

/**
 * Checks if the admin is logged in.
 * If not, redirects to login.html.
 * Call this at the top of every admin page's script.
 */
function requireAuth() {
  const auth = sessionStorage.getItem('adminAuth');
  if (!auth) {
    window.location.href = '/admin/login.html';
    return false;
  }
  return true;
}

/**
 * Returns the Authorization header needed for admin API calls.
 * Usage: fetch('/api/admin/...', { headers: authHeaders() })
 */
function authHeaders() {
  const auth = sessionStorage.getItem('adminAuth');
  return {
    'Authorization': 'Basic ' + auth,
    'Content-Type': 'application/json'
  };
}

/**
 * Returns auth headers without Content-Type (needed for multipart/form-data uploads).
 * When uploading files, the browser sets Content-Type automatically with the boundary.
 */
function authHeadersForUpload() {
  const auth = sessionStorage.getItem('adminAuth');
  return { 'Authorization': 'Basic ' + auth };
}

/**
 * Logs out and redirects to login page.
 */
function logout() {
  sessionStorage.removeItem('adminAuth');
  sessionStorage.removeItem('adminUsername');
  window.location.href = '/admin/login.html';
}

// ============================================================
// UI HELPERS — reusable status messages and loading states
// ============================================================

/**
 * Shows a success message in the element with id="statusMsg".
 * The message automatically disappears after 4 seconds.
 */
function showSuccess(message) {
  const el = document.getElementById('statusMsg');
  if (!el) return;
  el.textContent = message;
  el.className = 'alert alert-success';
  setTimeout(() => { el.className = 'alert alert-hidden'; }, 4000);
}

/**
 * Shows an error message in the element with id="statusMsg".
 */
function showError(message) {
  const el = document.getElementById('statusMsg');
  if (!el) return;
  el.textContent = message;
  el.className = 'alert alert-error';
}

/**
 * Shows a loading spinner text in a container element.
 */
function showLoading(containerId) {
  const el = document.getElementById(containerId);
  if (el) el.innerHTML = '<div class="loading">Loading…</div>';
}

// ============================================================
// API HELPERS — consistent fetch wrappers
// ============================================================

/**
 * Makes a GET request to the admin API.
 * @param {string} url - The API endpoint
 * @returns {Promise} The parsed JSON response
 */
async function adminGet(url) {
  const response = await fetch(url, { headers: authHeaders() });
  if (!response.ok) {
    const err = await response.json().catch(() => ({}));
    throw new Error(err.message || 'Request failed with status ' + response.status);
  }
  return response.json();
}

/**
 * Makes a POST request with JSON body to the admin API.
 */
async function adminPost(url, body) {
  const response = await fetch(url, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify(body)
  });
  if (!response.ok) {
    const err = await response.json().catch(() => ({}));
    throw new Error(err.message || 'Request failed with status ' + response.status);
  }
  return response.json();
}

/**
 * Makes a PUT request with JSON body to the admin API.
 */
async function adminPut(url, body) {
  const response = await fetch(url, {
    method: 'PUT',
    headers: authHeaders(),
    body: JSON.stringify(body)
  });
  if (!response.ok) {
    const err = await response.json().catch(() => ({}));
    throw new Error(err.message || 'Request failed with status ' + response.status);
  }
  return response.json();
}

/**
 * Makes a DELETE request to the admin API.
 * Returns true on success (204 No Content).
 */
async function adminDelete(url) {
  const response = await fetch(url, {
    method: 'DELETE',
    headers: authHeaders()
  });
  if (!response.ok) {
    const err = await response.json().catch(() => ({}));
    throw new Error(err.message || 'Delete failed with status ' + response.status);
  }
  return true;
}

/**
 * Uploads a file (multipart form data) to the admin API.
 * @param {string} url - The upload endpoint
 * @param {FormData} formData - The form data containing the file
 */
async function adminUpload(url, formData) {
  const response = await fetch(url, {
    method: 'POST',
    headers: authHeadersForUpload(), // No Content-Type — browser sets it for multipart
    body: formData
  });
  if (!response.ok) {
    const err = await response.json().catch(() => ({}));
    throw new Error(err.message || 'Upload failed with status ' + response.status);
  }
  return response.json();
}

// ============================================================
// SIDEBAR — mark current page as active
// ============================================================
document.addEventListener('DOMContentLoaded', () => {
  // Highlight the current page in the sidebar nav
  const currentPath = window.location.pathname;
  document.querySelectorAll('.sidebar-nav a').forEach(link => {
    if (link.getAttribute('href') === currentPath ||
        currentPath.endsWith(link.getAttribute('href'))) {
      link.classList.add('active');
    }
  });

  // Show the logged-in username if available
  const usernameEl = document.getElementById('sidebarUsername');
  if (usernameEl) {
    usernameEl.textContent = sessionStorage.getItem('adminUsername') || 'Admin';
  }
});
