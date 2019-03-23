# Generated by Django 2.1.7 on 2019-03-23 12:43

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Bindings',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('start_point', models.TextField()),
                ('end_point', models.TextField()),
            ],
        ),
        migrations.CreateModel(
            name='Buses',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('bus_id', models.UUIDField(unique=True)),
                ('gosnum', models.CharField(max_length=9)),
            ],
        ),
        migrations.CreateModel(
            name='Clients',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('client_id', models.UUIDField(unique=True)),
                ('disability_card', models.CharField(max_length=60)),
                ('approved', models.BooleanField()),
            ],
        ),
        migrations.AddField(
            model_name='bindings',
            name='bus_id',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='booking.Buses'),
        ),
        migrations.AddField(
            model_name='bindings',
            name='client_id',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='booking.Clients'),
        ),
    ]