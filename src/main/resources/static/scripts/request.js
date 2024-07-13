$(document).ready(function () {
    let isLoggedIn = checkIfLoggedIn();
   checkIfModerator(function () {
       $('#nav-agencies').removeAttr('hidden');
       $('#nav-req').removeAttr('hidden');
       $('#nav-admin-map').removeAttr('hidden');
   }, function () {
       if (!!isLoggedIn) {
           $('#nav-user-map').removeAttr('hidden');
       }
   });
   $('#nav-logout').click(function () { userLogout() });
});

function createErrorModal() {
    const content = `
        <div class="modal fade" id="errorModal">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <button id="errorModalCloseBtn" class="btn-close"></button>
                    </div>
                    <div class="modal-body text-center">
                        <h2 id="error-message"></h2>
                    </div>
                </div>
            </div>
        </div>
        `;

    $('#pageContent').append(content);
    registerCloseErrorModalEvent();
}

function registerCloseErrorModalEvent() {
    $('#errorModalCloseBtn').click(function () {
        $('#errorModal').modal('hide');
        window.location.reload();
    });
}

function displayErrorMessage(err) {
    $('#error-message').text(err.responseText);
    $('#errorModal').modal('show');
}

function checkIfModerator(event, eventIsLoggedIn) {
    makeJsonRequest("/api/user", "GET", null, 'json',function (res) {
       if (res.isModerator !== true) {
           if (eventIsLoggedIn !== null) {
               eventIsLoggedIn();
           }

           return
       }

        if (event !== null)
            event(res);

    }, null);
}

function checkIfLoggedIn(eventIfNotLoggedIn) {
    const token = sessionStorage.getItem('token')
    if (token == null) {
        if (eventIfNotLoggedIn !== null) {
            eventIfNotLoggedIn();
        }
        return false;
    }

    $('#nav-login').hide();
    $('#nav-logout').removeAttr('hidden');
    $('#nav-bookings').removeAttr('hidden');
    return true;
}

function checkForLogin() {
    const token = sessionStorage.getItem('token')
    if (token == null)
        window.location.href = "/login";
}

function makeJsonRequest(url, method, data, dataType, successCallback, errorCallback) {
    const headers = {
        "Content-type": "application/json"
    };

    const token = sessionStorage.getItem('token')
    if (token !== null)
        headers.Authorization = "Bearer " + token;

    $.ajax({
        url: url,
        method: method,
        headers: headers,
        data: JSON.stringify(data),
        dataType: dataType,
        success: [function (response) {
            if (successCallback) {
                successCallback(response);
            }
        }],
        error: [function (xhr, textStatus, errorThrown) {
            if (errorCallback) {
                errorCallback(xhr, textStatus, errorThrown);
            }
        }]
    });
}

function userLogout() {
    const token = sessionStorage.getItem('token')
    const refresh = sessionStorage.getItem('refresh')
    if (token === null || refresh === null)
        return;

    const userInfo = {
        access_token: token,
        refresh_token: refresh
    }

    makeJsonRequest("/api/logout", "POST", userInfo, 'text', function() {
       sessionStorage.removeItem('token');
       sessionStorage.removeItem('refresh');
       window.location.href = "/";
    });
};

function postForm(url, formData, successCallback, errorCallback) {
    let headers = {};

    const token = sessionStorage.getItem('token')
    if (token !== null)
        headers.Authorization = "Bearer " + token;

    $.ajax({
        url: url,
        type: 'POST',
        data: formData,
        headers: headers,
        processData: false,
        contentType: false,
        cache: false,
        success: [function (response) {
            if (successCallback) {
                successCallback(response);
            }
        }],
        error: [function (xhr, textStatus, errorThrown) {
            if (errorCallback) {
                errorCallback(xhr, textStatus, errorThrown);
            }
        }]
    });
}


