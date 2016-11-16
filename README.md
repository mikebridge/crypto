```bash

encryption:

$ gpg2 --batch --yes --passphrase-file key.txt -z 0 --cipher-algo AES256 --armor --output "output.txt" --symmetric "input.txt"

decryption:

$ gpg2 --batch --yes --passphrase-file key.txt --output "output.txt" --decrypt "input.txt"

