/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\SmartGit\\workspace\\MyProject\\player_pad_new\\app\\src\\cn\\pipi\\mobile\\pipiplayer\\local\\vlc\\interfaces\\IAudioServiceCallback.aidl
 */
package cn.pipi.mobile.pipiplayer.local.vlc.interfaces;
public interface IAudioServiceCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements cn.pipi.mobile.pipiplayer.local.vlc.interfaces.IAudioServiceCallback
{
private static final java.lang.String DESCRIPTOR = "cn.pipi.mobile.pipiplayer.local.vlc.interfaces.IAudioServiceCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an cn.pipi.mobile.pipiplayer.local.vlc.interfaces.IAudioServiceCallback interface,
 * generating a proxy if needed.
 */
public static cn.pipi.mobile.pipiplayer.local.vlc.interfaces.IAudioServiceCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof cn.pipi.mobile.pipiplayer.local.vlc.interfaces.IAudioServiceCallback))) {
return ((cn.pipi.mobile.pipiplayer.local.vlc.interfaces.IAudioServiceCallback)iin);
}
return new cn.pipi.mobile.pipiplayer.local.vlc.interfaces.IAudioServiceCallback.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_update:
{
data.enforceInterface(DESCRIPTOR);
this.update();
reply.writeNoException();
return true;
}
case TRANSACTION_updateProgress:
{
data.enforceInterface(DESCRIPTOR);
this.updateProgress();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements cn.pipi.mobile.pipiplayer.local.vlc.interfaces.IAudioServiceCallback
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void update() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_update, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void updateProgress() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_updateProgress, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_update = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_updateProgress = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public void update() throws android.os.RemoteException;
public void updateProgress() throws android.os.RemoteException;
}
