# Ocir - Social Media Application

## Windows дээр суулгах заавар

### Шаардлага
- Windows 10/11 (64-bit)
- Java 17+ суусан байх

### Суулгах

1. `.exe` installer-ыг татах:

   👉 [Ocir Windows Installer татах](https://drive.google.com/file/d/1ItEk2OC6VJi8Ls_-Pohm2YFvpEJfJHzT/view?usp=drive_link)

2. Татаж авсан `.exe` файлыг ажиллуулж, зааврын дагуу суулгана.

---

## Linux дээр суулгах заавар

### Шаардлага
- Ubuntu/Debian/Kali Linux (amd64)
- Java 17+ суусан байх

### Суулгах

1. `.deb` файлыг татах:
```bash
wget https://github.com/milo475/ocir.mn/raw/main/ocir/target/installer/ocir_1.0.0_amd64.deb
```

2. Суулгах:
```bash
sudo dpkg -i ocir_1.0.0_amd64.deb
```

3. Хэрэв dependency алдаа гарвал:
```bash
sudo apt-get install -f
```

### Ажиллуулах

```bash
/opt/ocir/bin/Ocir
```

### Устгах

```bash
sudo dpkg -r ocir
```
