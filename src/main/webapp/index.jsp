<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Jersey RESTful Web App</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    
</head>
<body>

<div class="container mt-4">
    <h2 class="text-center mb-4">Jersey RESTful Web Application</h2>

    <!-- Bootstrap Nav Tabs -->
    <ul class="nav nav-tabs" id="appTabs">
        <li class="nav-item">
            <a class="nav-link active" data-bs-toggle="tab" href="#profileTab">User Profile</a>
        </li>
        <li class="nav-item">
            <a class="nav-link" data-bs-toggle="tab" href="#messagesTab">User Messages</a>
        </li>
        <li class="nav-item">
            <a class="nav-link" data-bs-toggle="tab" href="#translateTab">Translate Message</a>
        </li>
    </ul>

    <div class="tab-content mt-3">
        <!-- User Profile Tab -->
        <div id="profileTab" class="tab-pane fade show active">
            <h4>User Profile</h4>
            <button class="btn btn-primary" onclick="loadProfiles()">Load Profile</button>
            <table class="table mt-3">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>First Name</th>
                        <th>Last Name</th>
                        <th>Profile Name</th>
                    </tr>
                </thead>
                <tbody id="profileTable"></tbody>
            </table>
        </div>

        <!-- User Messages Tab -->
        <div id="messagesTab" class="tab-pane fade">
            <h4>User Messages</h4>
            <button class="btn btn-primary" onclick="loadMessages()">Load Messages</button>
            <table class="table mt-3">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Author</th>
                        <th>Message</th>
                        <th>Created</th>
                    </tr>
                </thead>
                <tbody id="messagesTable"></tbody>
            </table>
        </div>

        <!-- Translate Message Tab -->
        <div id="translateTab" class="tab-pane fade">
            <h4>Translate Message</h4>
            <div class="mb-3">
                <label for="messageId" class="form-label">Message ID:</label>
                <input type="number" class="form-control" id="messageId" placeholder="Enter message ID">
            </div>
            <div class="mb-3">
                <label for="targetLang" class="form-label">Target Language (e.g., es, ja, fr):</label>
                <input type="text" class="form-control" id="targetLang" placeholder="Enter language code">
            </div>
            <button class="btn btn-success" onclick="translateMessage()">Translate</button>
            <h5 class="mt-3">Translated Message:</h5>
            <p id="translatedMessage" class="alert alert-info d-none"></p>
        </div>
    </div>
</div>

<!-- Bootstrap Bundle with Popper.js -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<script>
    function loadProfiles() {
        $.ajax({
            url: 'http://localhost:8080/webapi/profiles',
            type: 'GET',
            success: function(data) {
                console.log("API Response:", data); // Check if the API response is correct
                if (data.length === 0) {
                    $("#profileTable").html("<tr><td colspan='4'>No profiles available</td></tr>");
                    return;
                }
                let tableContent = "";
                data.forEach(profile => {
                    console.log("Processing Profile:", profile); // Debugging each profile object
                    // Explicitly checking if each property exists and is valid
                    const id = profile.id || "N/A";
                    const firstName = profile.firstName || "N/A";
                    const lastName = profile.lastName || "N/A";
                    const profileName = profile.profileName || "N/A";

                    tableContent += 
                        "<tr>" +
                            "<td>" + id + "</td>" +
                            "<td>" + firstName + "</td>" +
                            "<td>" + lastName + "</td>" +
                            "<td>" + profileName + "</td>" +
                        "</tr>";
                });
                console.log('tableContent', tableContent);
                $("#profileTable").html(tableContent);
            },
            error: function(xhr, status, error) {
                console.error("Error:", error); // Logs the error details
                alert("Failed to load profiles. Please check the console for details.");
            }
        });
    }

  


    function loadMessages() {
        $.ajax({
            url: 'http://localhost:8080/webapi/messages/',
            type: 'GET',
            success: function(data) {
                console.log("API Response:", data);

                if (!Array.isArray(data)) {
                    console.error("Unexpected API response format:", data);
                    return;
                }

                if (data.length === 0) {
                    $("#messagesTable tbody").html("<tr><td colspan='4'>No messages available</td></tr>");
                    return;
                }

                let tableContent = "";
                data.forEach(function(message) {
                    console.log("Processing Message:", message);

                    const id = message.id || "N/A";
                    const author = message.author || "N/A";
                    const text = message.message || "N/A";
                    const created = message.created || "N/A";

                    tableContent += 
                        "<tr>" +
                            "<td>" + id + "</td>" +
                            "<td>" + author + "</td>" +
                            "<td>" + text + "</td>" +
                            "<td>" + created + "</td>" +
                        "</tr>";
                });

                console.log('tableContent:', tableContent);
                $("#messagesTable").html(tableContent);
            },
            error: function(xhr, status, error) {
                console.error("Error:", error);
                alert("Failed to load messages. Please check the console for details.");
            }
        });
    }
   


    function translateMessage() {
        let messageId = $("#messageId").val();
        let targetLang = $("#targetLang").val();

        if (!messageId || !targetLang) {
            alert("Please enter both Message ID and Target Language.");
            return;
        }
        
        const url = "http://localhost:8080/webapi/messages/translate/" + targetLang + "/" + messageId
        
        $.ajax({
            url,
            type: 'GET',
            success: function(data) {
                $("#translatedMessage").removeClass("d-none").text("Original: " + data.originalMessage + " | Translated: " + data.translatedMessage);
            },
            error: function() {
                alert("Failed to translate message.");
            }
        });
    }
</script>

</body>
</html>