# Minecraft Chat → Discord Webhook Mod

Simple Minecraft mod for **1.21.11** that sends in-game chat messages to Discord using webhooks.

## Features

* Sends chat messages to Discord
* Uses webhooks (no bot needed)
* Ignores commands (messages starting with `/`)

## Installation

1. Install Minecraft 1.21.11
2. Install Fabric
3. Put the mod `.jar` into the `mods` folder

## Setup

1. Create a Discord webhook
2. Copy the webhook URL
3. Paste it into config:

```json
{
  "webhook_url": "your_webhook_here"
}
```

## Usage

Run the game and send a message in chat — it will appear in Discord.

Commands like `/login` are not sent.
