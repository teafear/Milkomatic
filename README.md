# Milkomatic 🐮

Плагин для автоматического доения животных в Minecraft с помощью диспенсеров.  
**Версии Minecraft**: 1.17 – 1.21 | **Требует Java 17+**

[![Скачать](https://img.shields.io/badge/Скачать-v2.2-blue)](https://github.com/teafear/Milkomatic/releases)
[![Лицензия](https://img.shields.io/badge/Лицензия-MIT-green)](LICENSE)

---

## 📦 Установка
1. Скачайте `Milkomatic.jar` из [раздела Releases](https://github.com/teafear/Milkomatic/releases).
2. Переместите файл в папку `plugins/` вашего сервера.
3. Перезагрузите сервер командой `/reload` или перезапустите его.

---

## 🎮 Использование
1. Поставьте **диспенсер** рядом с коровой, грибной коровой или козой.
2. Загрузите в него **пустое ведро**.
3. Активируйте красной пылью — получите молоко!

**Поддерживаемые мобы**:
- Обычные коровы → 🥛 Ведро молока
- Грибные коровы → 🍄 Подозрительный рагу
- Козы (не кричащие) → 🥛 Ведро молока
- Пораженные молнией → 🥛 Ведро молока

---

## ⚙️ Конфигурация
Измените настройки в `plugins/Milkomatic/config.yml`:
```yaml
settings:
  auto-collect: true # Автоматически забирать молоко в инвентарь
  search-radius: 1.5 # Радиус поиска мобов
  sound-effects: true # Звуки доения

custom-drops:
  LIGHTNING_STRUCK_COW: HONEY_BOTTLE # Мобы после удара молнией
```
<a href="https://modrinth.com/plugin/milkomatic">
  <img src="https://cdn2.steamgriddb.com/grid/54bed33a9a7f84346018192fe26d4357.png" alt="Скачать" width="200" />
</a>
