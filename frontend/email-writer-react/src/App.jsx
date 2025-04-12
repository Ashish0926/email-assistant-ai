import { useState } from 'react';
import axios from 'axios';
import './App.css';

function App() {
  const [emailContent, setEmailContent] = useState('');
  const [tone, setTone] = useState('');
  const [generatedReply, setGeneratedReply] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async () => {
    console.log("handleSubmit called");
    setError('');
    setGeneratedReply('');
    setLoading(true);
    const url = import.meta.env.VITE_EMAIL_REPLY_API_URL;

    try {
      console.log("Sending request to backend: ", url);
      const response = await axios.post(url, {
        emailContent: emailContent,
        tone: tone
      });

      console.log("Response from backend:", response);
      setGeneratedReply(response.data);
    } catch (err) {
      setError('Failed to generate reply. Please try again.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCopy = () => {
    navigator.clipboard.writeText(generatedReply);
  };

  return (
    <div className="flex justify-center align-middle min-h-screen bg-base-100 p-5">
      <div className="w-full max-w-2xl">
        <header className="text-3xl font-bold text-center mt-10 mb-8">Email Reply Generator</header>

        <textarea
          className="textarea textarea-bordered w-full min-h-[150px]"
          placeholder="Paste your email here..."
          value={emailContent}
          onChange={(e) => setEmailContent(e.target.value)}
        ></textarea>

        <select
          className="select select-bordered w-full mt-6"
          value={tone}
          onChange={(e) => setTone(e.target.value)}
        >
          <option disabled value="">Select tone</option>
          <option value="">None</option>
          <option value="Professional">Professional</option>
          <option value="Friendly">Friendly</option>
          <option value="Angry">Angry</option>
        </select>

        <button
          onClick={handleSubmit}
          disabled={!emailContent || loading}
          className="btn btn-primary mt-6 w-full"
        >
          {loading && <span className="loading loading-spinner mr-2"></span>}
          Generate Reply
        </button>

        {error && (
          <div className="alert alert-error mt-4">
            <span>{error}</span>
          </div>
        )}

        {generatedReply && (
          <div className="mt-6">
            <label className="label">
              <span className="label-text font-bold">Generated Reply</span>
            </label>
            <textarea
              className="textarea textarea-bordered w-full min-h-[150px]"
              value={generatedReply}
              readOnly
            ></textarea>
            <button onClick={handleCopy} className="btn btn-outline btn-success mt-4 w-full">
              Copy to Clipboard
            </button>
          </div>
        )}
      </div>
    </div>
  );
}

export default App;
