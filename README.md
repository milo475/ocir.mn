# Ocir - Social Media Application

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
