# Subscription Calculator

A dependency-free Java subscription billing calculator with:

- Monthly billing on the 1st of every month
- DTOs for calculator input/output
- Java backend using the JDK HTTP server
- Static browser UI for manual testing
- Unit tests runnable with plain `javac`/`java`

## Run Tests

```powershell
.\scripts\test.ps1
```

## Run Backend and UI

```powershell
.\scripts\run.ps1
```

Then open:

```text
http://localhost:8080
```

## Example Input

```json
{
  "billing_frequency": "monthly",
  "currency": "USD",
  "line_items": [
    {
      "product": "prod1",
      "price_model": "flat",
      "unit_price": 500,
      "quantity": 3,
      "discount": {
        "type": "percentage",
        "value": 10
      }
    }
  ]
}
```

