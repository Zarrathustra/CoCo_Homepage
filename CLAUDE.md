## gpt-image-2 Image Generation

Use the `bbdinoapi.top` image generation endpoint for `gpt-image-2`.

- Store the API key in the local `.env` file at `.env` as `API_KEY=...`.
- Load the key from `.env` before calling the API.
- Do not hard-code the key in commands that are meant to be saved or shared.

Example call:

```bash
set -a
. ./.env
set +a

curl https://bbdinoapi.top/v1/images/generations \
  -H "Authorization: Bearer ${API_KEY}" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "gpt-image-2",
    "prompt": "description",
    "size": "1024x1024",
    "quality": "high",
    "n": 1
  }'
```

Request fields:

- `model` string, required: fixed to `gpt-image-2`.
- `prompt` string, required: describes the image to generate.
- `size` string, optional: `1024x1024`, `1792x1024`, `1024x1792`, `2560x1440`, or `2160x3840`.
- `quality` string, optional: `low`, `medium`, or `high`.
- `n` number, optional: number of images to generate; currently prefer `1`-`4`.
