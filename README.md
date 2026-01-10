# Somaliland Global Currency Exchange API  💸
 <pre>
🟩🟩🟩🟩🟩
⬜️⬜️⭐️⬜️⬜️
🟥🟥🟥🟥🟥
</pre>

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green?style=flat-square)
![Database](https://img.shields.io/badge/PostgreSQL-16-blue?style=flat-square)
![License](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-square)

A secure, open-source RESTful microservice that provides the first-ever algorithmic bridge between the **Somaliland Shilling (SLSH)** and global currencies.

---

## 🚀 The Problem
Somaliland functions as a self-declared state with its own currency, the **Somaliland Shilling (SLSH)**. Because the region is not fully integrated into the global SWIFT banking system:
* ❌ **No Public Data:** There are no digital exchange rates available for developers.
* ❌ **Manual Chaos:** Local businesses and logistics companies must calculate rates manually, leading to billing errors.
* ❌ **Integration Gap:** Apps cannot programmatically convert `SLSH` to `USD`, `EUR`, or `KES`.

## 💡 The Solution
**Somaliland FX API** acts as a centralized "source of truth." It combines **local street rates** (managed securely by authorized admins) with **live global market data** to enable accurate, real-time conversion for any currency pair.

---

## ⚙️ The "Triangulation" Algorithm
Since there is no direct foreign exchange market for `SLSH -> EUR` or `SLSH -> KES`, the system uses **USD** as a pivot currency to bridge the gap mathematically.

**The Formula:**
$$\text{Target Rate} = \left( \frac{\text{Input SLSH}}{\text{Local USD Rate}} \right) \times \text{Live Global Rate}$$

**Example (SLSH to KES):**
1.  **Local Leg:** User has 8,500 SLSH. System knows `1 USD = 8,500 SLSH`.
    * *Result: $1.00 USD*
2.  **Global Leg:** System fetches live market data: `1 USD = 129.50 KES`.
    * *Calculation: 1.00 * 129.50*
3.  **Final Result:** 129.50 KES.

---

## 🛠 Tech Stack
* **Core Framework:** Java 17, Spring Boot 3.x
* **Database:** PostgreSQL (Relational Data Persistence)
* **Security:** Custom `X-ADMIN-KEY` Header Authentication
* **HTTP Client:** `RestTemplate` for external API integration
* **Tooling:** Maven, Docker (planned), Git

---

## 🔌 API Endpoints

### 1. Public Access (Read-Only)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/rates/convert` | Converts SLSH to any global currency (e.g., USD, EUR). |
| `GET` | `/api/rates/convert-to-slsh` | Converts any global currency back to SLSH. |
| `GET` | `/api/rates/all` | Returns a history of all exchange rate updates. |

### 2. Admin Access (Protected 🔒)
| Method | Endpoint | Description | Security Header |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/rates/update` | Updates the base `SLSH/USD` rate. | `X-ADMIN-KEY` |

> **Note:** Protected endpoints require the `X-ADMIN-KEY` header. Requests without it will be rejected with `401 Unauthorized`.

---

## 📦 Installation & Setup

### Prerequisites
* Java 17 SDK
* PostgreSQL installed and running
* Maven

### Step 1: Clone the Repository
```bash
git clone [https://github.com/israad-visuals/somaliland-fx-api.git](https://github.com/israad-visuals/somaliland-fx-api.git)
cd somaliland-fx-api

## 📂 Project Structure
Here is a high-level overview of the backend architecture:

src/main/java/com/somaliland/exchange
├── controller
│   └── ExchangeRateController.java  # REST Endpoints (The "Door")
├── service
│   └── ExchangeRateService.java     # Business Logic & Triangulation Math (The "Brain")
├── repository
│   └── ExchangeRateRepository.java  # Database Access (The "Memory")
└── model
    └── ExchangeRate.java            # Database Entity (The Data Shape)
