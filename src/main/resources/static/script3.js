// JavaScript function to handle form submission
async function getAiResponse(event) {
    event.preventDefault(); // Prevent default form submission

    const prompt = document.getElementById('prompt').value;
    const loadingMessage = document.getElementById('loading-message');
    const aiResponse = document.getElementById('ai-response');

    // Show the loading message in the aiResponse div and clear the previous response
    aiResponse.textContent = ''; // Clear previous response
    loadingMessage.textContent = 'Generating...'; // Display the loading message

    try {
        const response = await fetch(`/ai/generateStream?prompt=${encodeURIComponent(prompt)}`);
        const data = await response.text(); // Assuming the response is plain text

        // Hide the loading message and display the response
        loadingMessage.textContent = ''; // Clear loading message
        aiResponse.textContent = data;
    } catch (error) {
        console.error('Error fetching AI response:', error);
        loadingMessage.textContent = ''; // Clear loading message

        // Display error message in aiResponse div
        aiResponse.textContent = 'An error occurred while fetching the response.';
    }
}

// T