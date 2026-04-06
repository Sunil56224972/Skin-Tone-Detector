# 🎨 Skin Tone Detector — AI-Powered Microservice

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange?logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.4-brightgreen?logo=springboot)
![Docker](https://img.shields.io/badge/Docker-Ready-blue?logo=docker)
![Gemini AI](https://img.shields.io/badge/Google-Gemini_AI-4285F4?logo=google&logoColor=white)
![Status](https://img.shields.io/badge/Status-Production--Ready-success)
![License](https://img.shields.io/badge/License-MIT-yellow)
![PRs Welcome](https://img.shields.io/badge/PRs-Welcome-brightgreen?logo=github)

<br/>

**A high-performance, Domain-Driven Design (DDD) skin tone detection microservice powered by Google Gemini AI.**  
Modernizes legacy Bayesian logic with a swappable Strategy Pattern for blazing-fast, accurate skin analysis.

[Getting Started](#-getting-started) • [API Docs](#️-api-documentation) • [Docker](#-deployment-docker) • [Contributing](#-contributing) • [Report a Bug](#-found-a-bug-or-error)

<br/>

### 🧰 Built With

<img src="https://skillicons.dev/icons?i=java,spring,html,docker,maven,git,github&theme=dark" alt="Tech Stack Icons" />

</div>

---

## 📌 Table of Contents

- [About the Project](#-about-the-project)
- [Tech Stack](#-tech-stack)
- [Getting Started](#-getting-started)
- [Environment Setup (.env)](#-environment-setup-env-file)
- [API Documentation](#️-api-documentation)
- [Docker Deployment](#-deployment-docker)
- [Scalability Roadmap](#-scalability-roadmap)
- [Security & Validation](#-security--validation)
- [Found a Bug or Error?](#-found-a-bug-or-error)
- [Contributing](#-contributing)
- [License](#-license)

---

## 🧠 About the Project

**Skin Tone Detector** is a production-ready REST microservice that analyzes images to detect and classify skin tone regions. It leverages **Google Gemini AI** alongside traditional computer vision algorithms (Bayesian & Heuristic) using a clean **Strategy Pattern** architecture — making it easy to swap or extend detection engines without changing business logic.

Built with **Spring Boot 3.4.4** on **Java 21**, it is containerized with Docker and designed to scale from a single instance to a distributed cloud-native system.

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 (LTS) |
| Framework | Spring Boot 3.4.4 |
| AI Engine | Google Gemini AI |
| Build Tool | Maven 3.9+ |
| Containerization | Docker |
| Architecture | Domain-Driven Design (DDD) + Strategy Pattern |

---

## 🚀 Getting Started

### Prerequisites

Make sure you have the following installed before running this project:

- ✅ **Java 21 (LTS)** or higher → [Download](https://adoptium.net/)
- ✅ **Maven 3.9+** (or use the included `./mvnw` wrapper)
- ✅ **Docker** (optional, for containerized deployment) → [Download](https://www.docker.com/)
- ✅ **Google Gemini API Key** → [Get yours here](https://aistudio.google.com/app/apikey)

### Installation & Run

```bash
# 1. Clone the repository
git clone https://github.com/Sunil56224972/Skin-Tone-Detector.git

# 2. Navigate into the project directory
cd Skin-Tone-Detector

# 3. Set up your .env file (see section below ⬇️)

# 4. Run the application
./mvnw spring-boot:run
```

The server will start on **`http://localhost:8080`** 🎉

---

## 🔐 Environment Setup (.env File)

> ⚠️ **This step is required before running the project.**

This project uses a `.env` file to securely manage your **Google Gemini API Key**. Without it, the AI-powered skin analysis feature will not work.

### Step 1 — Create your `.env` file

In the **root directory** of the project, create a file named `.env`:

```bash
touch .env
```

### Step 2 — Add your Gemini API Key

Open the `.env` file and add the following:

```env
GEMINI_API_KEY=your_gemini_api_key_here
```

Replace `your_gemini_api_key_here` with your actual key from [Google AI Studio](https://aistudio.google.com/app/apikey).

### Step 3 — You're ready to run! 🚀

```bash
./mvnw spring-boot:run
```

> 🔒 **Security Note:** The `.env` file is already listed in `.gitignore` — it will **never** be committed to the repository. Never share your API key publicly.

---

## 🛠️ API Documentation

### **Detect Skin Tone**

Analyze an image to identify and classify skin-tone regions using either **Heuristic**, **Bayesian**, or **Gemini AI** algorithms.

**Endpoint:** `POST /api/v1/processor/detect-skin`

| Parameter | Type | Required | Description |
|---|---|---|---|
| `image` | `MultipartFile` | ✅ Yes | Target image (JPEG or PNG) |
| `strategy` | `String` | ❌ Optional | `bayesian` (default) \| `heuristic` |
| `format` | `String` | ❌ Optional | `overlay` (default) \| `mask` |

**Success Response — `200 OK`:**

- Returns binary image data (PNG format)
- Includes response header: `X-Skin-Percentage: [0-100]`

---

### CURL Examples

**1. Detect skin with visual overlay (default):**
```bash
curl -X POST -F "image=@photo.jpg" \
     "http://localhost:8080/api/v1/processor/detect-skin?strategy=bayesian&format=overlay" \
     -o highlighted_photo.png
```

**2. Generate a binary skin mask:**
```bash
curl -X POST -F "image=@photo.png" \
     "http://localhost:8080/api/v1/processor/detect-skin?format=mask" \
     -o skin_mask.png
```

**3. Use heuristic strategy:**
```bash
curl -X POST -F "image=@photo.jpg" \
     "http://localhost:8080/api/v1/processor/detect-skin?strategy=heuristic" \
     -o result.png
```

---

## 🐋 Deployment (Docker)

Containerize and deploy the service anywhere — Kubernetes, AWS, GCP, or Azure.

```bash
# Build the Docker image
docker build -t skin-detector-api .

# Run the container (pass your API key as env variable)
docker run -p 8080:8080 \
  -e GEMINI_API_KEY=your_gemini_api_key_here \
  skin-detector-api
```

The API will be available at **`http://localhost:8080`**.

---

## 📈 Scalability Roadmap

This architecture is built to evolve alongside increasing load and enterprise demands.

| Phase | Description | Technologies |
|---|---|---|
| **Phase 1 — Current** | Synchronous REST processing for real-time analysis | Spring Boot, Strategy Pattern |
| **Phase 2 — Distribution** | Cloud-native object storage for high availability | AWS S3, Azure Blob Storage |
| **Phase 3 — Async Engine** | Worker-queue architecture to decouple analysis | RabbitMQ, Redis Streams |
| **Phase 4 — ML Integration** | Deep Learning inference to replace Bayesian strategy | ONNX, TensorFlow Serving |

---

## 🛡 Security & Validation

- 🔒 **Strict Content-Type Validation** — Rejects any file that is not `image/png` or `image/jpeg`
- ⚠️ **Global Error Handling** — Standardized JSON error responses for all exceptions
- 🔑 **API Key Protection** — Gemini key is loaded via environment variable, never hardcoded
- 🚫 **Stateless Design** — No session affinity, ensuring security and horizontal scalability

---

## 🐛 Found a Bug or Error?

If you encounter any bug, unexpected behavior, or error while using this project — **please don't hesitate to reach out!**

### Here's how you can help:

1. **🔍 Check existing issues** — Search [open issues](https://github.com/Sunil56224972/Skin-Tone-Detector/issues) to see if it's already reported
2. **📝 Open a new issue** — If it's new, [create an issue](https://github.com/Sunil56224972/Skin-Tone-Detector/issues/new) with:
   - A clear description of the bug
   - Steps to reproduce it
   - Expected vs actual behavior
   - Screenshots or error logs (if available)
3. **🔧 Fix it and send a Pull Request** — If you know the fix, go ahead and [submit a PR](#-contributing)! I'd really appreciate it.

> 💬 **Every bug report helps make this project better. Thank you!**

---

## 🤝 Contributing

Contributions are what make the open source community amazing! Any contributions you make are **greatly appreciated**.

### How to Contribute

```bash
# 1. Fork the repository
# Click the "Fork" button at the top right of this page

# 2. Clone your fork
git clone https://github.com/YOUR_USERNAME/Skin-Tone-Detector.git

# 3. Create a new feature branch
git checkout -b feature/your-feature-name

# 4. Make your changes and commit
git commit -m "feat: add your feature description"

# 5. Push to your branch
git push origin feature/your-feature-name

# 6. Open a Pull Request
# Go to the original repo and click "New Pull Request"
```

### Contribution Guidelines

- 🧹 Keep code clean and well-commented
- ✅ Test your changes before submitting
- 📝 Write clear PR descriptions explaining what changed and why
- 🔗 Reference any related issues in your PR (e.g., `Closes #12`)

> 💡 Whether it's a bug fix, new feature, or documentation improvement — **all PRs are welcome!**

---

## 📄 License

Distributed under the **MIT License**. See [`LICENSE`](LICENSE) for more information.

---

## 👨‍💻 Author

**Sunil** — [@Sunil56224972](https://github.com/Sunil56224972)

<div align="center">

⭐ **If you found this project helpful, please give it a star!** ⭐

</div>
